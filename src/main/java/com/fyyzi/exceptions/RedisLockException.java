package com.fyyzi.exceptions;

import com.fyyzi.common.enums.CodeMsgEnum;

/**
 * Redis异常信息类
 *
 * @author 息阳
 */
public class RedisLockException extends AbstractBasicException {

    public RedisLockException(CodeMsgEnum codeMsgEnum) {
        super(codeMsgEnum);
    }

    public RedisLockException(CodeMsgEnum codeMsgEnum, Throwable exception) {
        super(codeMsgEnum, exception);
    }

    public RedisLockException(String code, String message, Throwable exception) {
        super(code, message, exception);
    }

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
}
