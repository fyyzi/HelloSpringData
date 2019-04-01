package com.fyyzi.entity;

import com.fyyzi.entity.base.animal.primate.People;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Table;

import javax.persistence.*;

/**
 * 用户User
 *
 * @author 息阳
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
@Entity
@Table(appliesTo = "user", comment = "一个注释")
public class User implements Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20) COMMENT '主键ID'")
    private Long id;

    /** 姓名 */
    private String userName;

    /** 年龄 */
    private Integer userAge;

    /** 部门ID */
    private Long departmentId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "peopleId")
    private People people;

}
