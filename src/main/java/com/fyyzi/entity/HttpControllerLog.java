package com.fyyzi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
public class HttpControllerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20) COMMENT '主键ID'")
    private Long id;

    /** 类型 */
    @Column(name = "type", columnDefinition = "char(2) COMMENT '0 包含request和response ; 1 只包含request;2只包含response'")
    private int type;

    /** 入参转Json */
    private String requestJson;

    /** 出参转Json */
    private String responseJson;

    /** 备注 */
    private String remarks;

    /** 创建人ID */
    private Long createUserId;

    /** 创建人名称 */
    private String createUserName;

    /** 创建时间 */
    private Date createDate;

}
