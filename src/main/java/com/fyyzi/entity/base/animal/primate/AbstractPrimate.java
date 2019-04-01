package com.fyyzi.entity.base.animal.primate;

import com.fyyzi.entity.base.animal.AbstractAnimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;

/**
 * 灵长类
 *
 * @author 羽化荼
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class AbstractPrimate extends AbstractAnimal {

    /** 头 */
    private int head;

    /** 手 */
    private int hand;

    /** 脚丫子 */
    private int foot;

}
