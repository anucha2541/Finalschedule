package com.example.expensemanager.Model;

public class Data {

    private int ammount;
    private String type;
    private String note;
    private int link_type;
    private String id;
    private long date_stmp;
    private int hour;
    private int min;




    public Data(int ammount, String type, String note, String id, String date, long date_stmp,int hour,int min,int link_type) {
        this.ammount = ammount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
        this.date_stmp = date_stmp;
        this.hour = hour;
        this.min = min;
        this.link_type = link_type;
    }

    public int getLink_type() {
        return link_type;
    }

    public void setLink_type(int link_type) {
        this.link_type = link_type;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public long getDate_stmp() {
        return date_stmp;
    }

    public void setDate_stmp(long date_stmp) {
        this.date_stmp = date_stmp;
    }

    public int getAmmount() {
        return ammount;
    }

    public void setAmmount(int ammount) {
        this.ammount = ammount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public Data (){



    }
}
