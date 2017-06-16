package com.maystrovyy.storage;

import org.springframework.stereotype.Component;

@Component
public class WeekStorage {

    private volatile int week;

    public void setWeek() {
        this.week = 1;
    }

}