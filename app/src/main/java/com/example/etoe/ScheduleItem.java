package com.example.etoe;

public class ScheduleItem {
    private int hour;
    private int min;
    private String ori;
    private String dest;
    private String title;

    public ScheduleItem(int hour, int min, String ori, String dest, String title) {
        this.hour = hour;
        this.min = min;
        this.ori = ori;
        this.dest = dest;
        this.title = title;
    }

    public String getTitle() { return title; }
    public String getOri(){ return ori; }
    public String getDest(){ return dest; }
    public int getHour(){ return hour; }
    public int getMin(){ return min; }
}