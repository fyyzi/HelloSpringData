package com.fyyzi.common.enums;

import lombok.Getter;

/**
 * 模块枚举类
 *
 * @author 息阳
 */
@Getter
public enum ModuleEnum {

    /** 服务器内部模块 */
    COMMON(000, "common", "公共模块", "common"),

    /** 对外暴露接口 */
    EXTERNAL(504, "api", "对外暴露接口", "api"),
    ;

    ModuleEnum(Integer moduleCode, String modulePackageName, String describe, String describeEn) {
        this.moduleCode = moduleCode;
        this.modulePackageName = modulePackageName;
        this.describe = describe;
        this.describeEn = describeEn;
    }

    /** 模块标识 */
    private Integer moduleCode;
    /** 模块简称(包名) */
    private String modulePackageName;
    /** 模块中文描述 */
    private String describe;
    /** 模块英文 */
    private String describeEn;
}
