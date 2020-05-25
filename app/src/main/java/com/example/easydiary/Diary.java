package com.example.easydiary;

public class Diary {

    private int id;
    private String title;
    private String desc;
    private String date;
    private byte[] image;
    private double lat;
    private double lng;

    public Diary(int id, String t, String d, String date, byte[] i, double lat, double lng){
        this.id = id;
        title = t;
        desc = d;
        this.date = date;
        image = i;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId(){ return id; }
    public String getTitle(){ return title; }
    public String getDesc(){ return desc; }
    public String getDate(){ return date; }
    public byte[] getImage(){ return image; }
    public double getLat(){ return lat; }
    public double getLng(){ return lng; }

}
