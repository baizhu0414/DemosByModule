package com.example.modulestepcounter.utils;

import java.lang.String;
public class StepEntity {
    public String curDate; //当天的日期
    public String steps;   //当天的步数

    public StepEntity(String curDate, String steps) {
        this.curDate = curDate;
        this.steps = steps;
    }

    public String getCurDate() {
        return curDate;
    }

    public void setCurDate(String curDate) {
        this.curDate = curDate;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String toString() {
        return "StepEntity{" +
                "curDate='" + curDate + '\'' +
                ", steps=" + steps +
                '}';
    }
}