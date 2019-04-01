package com.fyyzi.entity.base.animal;

import java.util.Calendar;
import java.util.UUID;

/**
 * DNA
 *
 * @author 我
 */
public class DNA {

    UUID uuid;
    long timeInMillis;

    public DNA() {
        uuid = UUID.randomUUID();
        timeInMillis = Calendar.getInstance().getTimeInMillis();
    }

}
