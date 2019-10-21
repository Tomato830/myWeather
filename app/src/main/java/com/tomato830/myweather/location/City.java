package com.tomato830.myweather.location;

import com.google.gson.Gson;

import com.tomato830.myweather.weather.Basic;

import java.util.ArrayList;



public class City {
    public ArrayList<Basic> basic;
    public String status;

    @Override
    public String toString() {
        return "City{" +
                "basic=" + basic +
                ", status='" + status + '\'' +
                '}';
    }

    //处理城市json数据
    public static City handleCity(String data){
        Gson gson=new Gson();
        return gson.fromJson(data,City.class);
    }

}
