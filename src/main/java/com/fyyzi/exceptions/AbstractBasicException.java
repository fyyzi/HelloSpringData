package com.fyyzi.exceptions;

import com.fyyzi.common.enums.CodeMsgEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础异常类
 *
 * @author 息阳
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractBasicException extends RuntimeException {

    /** 序列化版本号 */
    private static final long serialVersionUID = 1L;

    /** 错误编码 */
    private final String code;

    public AbstractBasicException(CodeMsgEnum codeMsgEnum) {
        this(codeMsgEnum, null);
    }

    public AbstractBasicException(CodeMsgEnum apiStatus, Throwable exception) {
        this(apiStatus.getCode(), apiStatus.getMessage(), exception);
    }

    public AbstractBasicException(String code, String message, Throwable exception) {
        super(message, exception);
        this.code = code;
    }

}
