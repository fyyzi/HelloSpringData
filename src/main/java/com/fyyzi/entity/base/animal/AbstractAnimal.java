package com.fyyzi.entity.base.animal;

import com.fyyzi.entity.base.anima.Anima;
import lombok.Data;

/**
 * 动物
 *
 * @author 羽化荼
 */
@Data
public abstract class AbstractAnimal implements Anima {

    /** DNA */
    private DNA dna;

    /** 智力 */
    private Double intelligence;

    /** 力量 */
    private Double force;

    /** 敏捷 */
    private Double agile;

    /** 体力 */
    private Double physicalStrength;

    public AbstractAnimal() {
        dna = new DNA();
    }

    @Override
    public String thinking() {
        return "Who I'm I?";
    }
}
