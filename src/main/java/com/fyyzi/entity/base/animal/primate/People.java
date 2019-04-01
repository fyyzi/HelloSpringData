package com.fyyzi.entity.base.animal.primate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.util.Date;
import java.util.Locale;

/**
 * 人类
 *
 * @author 羽化荼
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(appliesTo = "people", comment = "人")
public class People extends AbstractPrimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20) COMMENT '主键ID'")
    private Long id;

    /** 身份证号码 */
    private Long identificationNumber;

    /** 国家 */
    private Locale locale;

    /** 姓名 */
    private String name;

    /** 生日 */
    @Column(columnDefinition = "datetime not null default now() COMMENT '生日'")
    private Date birth;

}
