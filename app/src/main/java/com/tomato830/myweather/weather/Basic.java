package com.tomato830.myweather.weather;

public class Basic {
    public String location;
    public String cid;
    public String parent_city;
    public String admin_area;

    @Override
    public String toString() {
        return "Basic{" +
                "location='" + location + '\'' +
                ", cid='" + cid + '\'' +
                ", parent_city='" + parent_city + '\'' +
                ", admin_area='" + admin_area + '\'' +
                '}';
    }
}
