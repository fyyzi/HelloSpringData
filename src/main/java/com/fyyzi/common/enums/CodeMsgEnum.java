package com.fyyzi.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 异常信息枚举类
 *
 * @author 息阳
 */
@Getter
@AllArgsConstructor
public enum CodeMsgEnum {

    /** 服务正常无错误 */
    OK(ModuleEnum.COMMON, 0, "服务正常"),
    /** redis的Lock方法发生错误 */
    REDIS_LOCK_ERROR(ModuleEnum.COMMON, 1, "redis.Lock方法发生错误"),

    REDIS_LOCK_TIME_OUT(ModuleEnum.COMMON, 2, "设置Redis锁超时"),
    /** 被执行方法产生了一个Exception */
    REDIS_LOCK_THROW(ModuleEnum.COMMON, 3, "被执行方法产生了一个异常"),
    ;

    /**
     * 枚举构造器
     *
     * @param moduleEnum   {@link ModuleEnum}
     * @param errorCode    错误码
     * @param errorMessage 错误信息
     */
    CodeMsgEnum(ModuleEnum moduleEnum, int errorCode, String errorMessage) {
        this(moduleEnum, errorCode, errorMessage, null);
    }

    /** 模块枚举 */
    private ModuleEnum moduleEnum;
    /** 异常状态码 */
    private int errorCode;
    /** 异常信息 */
    private String message;
    /** 国际化i18n键 */
    private String i18nKey;

    /**
     * 获取返回值Code
     *
     * @return 3位模块名 + 3位错误码  例如:正确状态码: 000000
     */
    public String getCode() {
        Integer moduleCode;
        if (this == CodeMsgEnum.OK) {
            moduleCode = 0;
        } else {
            moduleCode = moduleEnum.getModuleCode();
        }
        String moduleCodeFormat = "%03";
        String errorCodeFormat = "%03";
        return String.format(moduleCodeFormat, moduleCode) + String.format(errorCodeFormat, this.errorCode);
    }


}
