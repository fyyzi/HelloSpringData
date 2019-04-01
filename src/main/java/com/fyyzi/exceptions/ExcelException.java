package com.fyyzi.exceptions;

import com.fyyzi.common.enums.CodeMsgEnum;

/**
 * Excel导出常信息类
 *
 * @author 息阳
 */
public class ExcelException extends AbstractBasicException {

    public ExcelException(CodeMsgEnum codeMsgEnum) {
        super(codeMsgEnum);
    }

    public ExcelException(CodeMsgEnum codeMsgEnum, Throwable exception) {
        super(codeMsgEnum, exception);
    }

    public ExcelException(String code, String message, Throwable exception) {
        super(code, message, exception);
    }

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;
}
