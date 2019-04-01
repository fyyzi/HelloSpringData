package com.fyyzi.service;

import com.fyyzi.common.enums.CodeMsgEnum;
import com.fyyzi.common.utils.BeanApplicationContextUtil;
import com.fyyzi.exceptions.AbstractBasicException;
import com.fyyzi.exceptions.RedisLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * 获取锁管理类,分布式锁管理类,使用时 将该接口注入,调用lock方法 将被锁方法 例如:
 * public void demo(){String lock = distributedLocker.lock("123", () -> { return invoke();});System.out.println(lock);}private String invoke(){return "";}
 *
 * @author 息阳
 */
public interface DistributedLocker {

    /**
     * 获取锁
     *
     * @param resourceName 锁的名称
     * @param worker       获取锁后的处理类
     * @param <T>          T
     * @return 处理完具体的业务逻辑要返回的数据
     */
    <T> T lock(String resourceName, AquiredLockWorker<T> worker);

    /**
     * 获取锁
     *
     * @param resourceName 锁的名称
     * @param worker       获取锁后的处理类
     * @param lockTime     锁多久
     * @param <T>          T
     * @return <T> T
     */
    <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime);

    /**
     * 使用时可以用lambda表达式   【()-> {return 被调用的方法}】
     *
     * @param <T> T
     */
    interface AquiredLockWorker<T> {
        /**
         * 获取锁后执行的动作
         *
         * @return T
         */
        T invokeAfterLockAquire();
    }
}

/**
 * RedisLock
 *
 * @author 息阳
 */
@Component
@Slf4j
class DistributedLockerImpl implements DistributedLocker {

    /** 分布式锁Key开头 */
    private static final String LOCKER_PREFIX = "CAMELOT_DISTRIBUTED_LOCK:";

    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker) {
        return lock(resourceName, worker, 0);
    }

    @Override
    public <T> T lock(String resourceName, AquiredLockWorker<T> worker, int lockTime) {
        CustomRedisLock redisLock = new CustomRedisLock(LOCKER_PREFIX + resourceName, lockTime);
        T result = null;
        try {
            redisLock.lock();
            result = worker.invokeAfterLockAquire();

        } catch (AbstractBasicException e) {
            // 被执行的方法发生自定义异常,该异常不属于RedisLock方法发生的异常,因此继续向上抛出
            throw e;
        } catch (Exception e) {
            throw new RedisLockException(CodeMsgEnum.REDIS_LOCK_THROW, e);
        } finally {
            redisLock.unlock();
        }
        return result;
    }

    /**
     * 自定义Redis锁
     *
     * @author 息阳
     */
    private class CustomRedisLock {
        /** 轮询lock时间 */
        private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;

        /** 锁KEY */
        private String lockKey;

        /** 锁超时时间，防止线程在入锁以后，无限的执行等待 */
        private int expireMsecs;
        /** 默认超时时间 60 * 1000 = 60秒 */
        private static final int DEFAULT_EXPIRE_MSECS = 60 * 1000;

        /** 锁等待时间，防止线程饥饿 */
        private int timeoutMsecs = 10 * 1000;

        private RedisTemplate redisTemplate;

        /** 是否锁定 */
        private volatile boolean locked = false;

        /**
         * 自定义Redis锁
         *
         * @param lockKey     Redis锁的key
         * @param expireMsecs Redis锁超时时间,如果时间小于等于0则使用默认时间 {@link #DEFAULT_EXPIRE_MSECS}
         */
        private CustomRedisLock(String lockKey, int expireMsecs) {
            this(null, lockKey, expireMsecs);
        }

        /**
         * 自定义Redis锁
         *
         * @param redisTemplate {@link RedisTemplate}
         * @param lockKey       Redis锁的Key
         * @param expireMsecs   Redis锁超时时间,如果时间小于等于0则使用默认时间 {@link #DEFAULT_EXPIRE_MSECS}
         */
        private CustomRedisLock(RedisTemplate redisTemplate, String lockKey, int expireMsecs) {
            if (expireMsecs > 0) {
                this.expireMsecs = DEFAULT_EXPIRE_MSECS;
            }
            if (redisTemplate == null) {
                redisTemplate = BeanApplicationContextUtil.getBean("redisTemplate", RedisTemplate.class);
            }
            this.lockKey = lockKey;
            this.redisTemplate = redisTemplate;
        }

        /** 获取Redis锁 */
        private synchronized void lock() throws InterruptedException {
            long a = System.currentTimeMillis() + timeoutMsecs + 1;
            while (a > System.currentTimeMillis()) {
                long expiresTime = System.currentTimeMillis() + expireMsecs + 1;
                // 空值创建
                if (this.setTimeToRedisKeyIfNotExist(lockKey, expiresTime)) {
                    locked = true;
                    return;
                }
                // 如果锁已经被设置，则取值并判断是否超时,若超时则可重新设置锁
                Long expiredTime = this.getExpiredTimeByRedisKey(lockKey);
                if (expiredTime < System.currentTimeMillis()) {
                    String value = String.valueOf(expiresTime);
                    String oldValueStr = this.getSet(lockKey, value);
                    if (oldValueStr == null || !oldValueStr.equals(value)) {
                        locked = true;
                        return;
                    }
                }
                wait(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
            }
            throw new RedisLockException(CodeMsgEnum.REDIS_LOCK_TIME_OUT);
        }

        private Long getExpiredTimeByRedisKey(final String key) {
            String valueByKey = getValueByKey(key);
            try {
                return Long.valueOf(valueByKey);
            } catch (NumberFormatException e) {
                return 0L;
            }
        }

        /**
         * 根据key获取Redis中的Value值
         *
         * @param key redisKey
         * @return redisValue
         */
        private String getValueByKey(final String key) {
            String result = null;
            try {
                result = (String) redisTemplate.execute((RedisConnection connection) -> {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] data = connection.get(serializer.serialize(key));
                    connection.close();
                    if (data == null) {
                        return null;
                    }
                    return serializer.deserialize(data);
                });
            } catch (Exception e) {
                log.error("获取 redis lock 异常, key : {}", key, e);
            }
            return result;
        }

        /**
         * 设置最新的Key并把旧的Key返还回来
         *
         * @param key   RedisKey
         * @param value 新Value
         * @return 旧Value
         */
        private String getSet(final String key, final String value) {
            Object obj = null;
            try {
                obj = redisTemplate.execute((RedisConnection connection) -> {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] ret = connection.getSet(serializer.serialize(key), serializer.serialize(value));
                    connection.close();
                    return serializer.deserialize(ret);
                });
            } catch (Exception e) {
                log.error("设置 redis lock 异常, key : {}", key, e);
            }
            return obj != null ? (String) obj : null;
        }

        /**
         * 若key不存在则 创建锁 否则返回false;
         * Set value for key, only if key does not exist.
         *
         * @param redisKey   redis 锁的 key
         * @param redisValue redis过期时间(与时间纪元比较的毫秒值)
         * @return 创建锁成功返回true 失败返回false
         */
        private boolean setNX(final String redisKey, final String redisValue) {
            try {
                return (boolean) redisTemplate.execute((RedisConnection connection) -> {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    byte[] key = serializer.serialize(redisKey);
                    byte[] value = serializer.serialize(redisValue);
                    Boolean success = connection.setNX(key, value);
                    connection.close();
                    return success;
                });
            } catch (Exception e) {
                log.error("设置 redis lock 异常, key : {}", redisKey, e);
            }
            return false;
        }

        /**
         * 若key不存在则 创建锁 否则返回false;
         * Set value for key, only if key does not exist.
         *
         * @param lockKey redis 锁的 key
         * @param time    redis过期时间(与时间纪元比较的毫秒值)
         * @return 创建锁成功返回true 失败返回false
         */
        private boolean setTimeToRedisKeyIfNotExist(final String lockKey, final Long time) {
            return setNX(lockKey, String.valueOf(time));
        }

        /** 删除Redis的Key（如果 {@link #locked}获取为true） */
        private synchronized void unlock() {
            if (locked && delete(lockKey)) {
                locked = false;
            }
        }

        /**
         * 根据 redisKey 删除对应的锁
         *
         * @param redisKey redis Key
         */
        private boolean delete(String redisKey) {
            return (boolean) redisTemplate.execute((RedisConnection connection) -> {
                StringRedisSerializer serializer = new StringRedisSerializer();
                byte[] serialize = serializer.serialize(redisKey);
                Long result = connection.del(serialize);
                connection.close();
                return result != null && result.intValue() == 1;
            });
        }

    }

}
