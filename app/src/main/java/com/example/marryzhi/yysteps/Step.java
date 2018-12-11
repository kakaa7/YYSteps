package com.example.marryzhi.yysteps;

public class Step {
    private String num;
    private String week;
    private String date;

    public Step(String num, String week, String date){
        this.num = num;
        this.week = week;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getNum() {
        return num;
    }

    public String getWeek() {
        return week;
    }
}
