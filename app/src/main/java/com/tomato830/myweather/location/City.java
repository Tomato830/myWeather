package com.tomato830.myweather.location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomato830.myweather.weather.Basic;

import java.util.ArrayList;



public class City {
    public Basic basic;
    public String status;


    //处理城市json数据
    public static ArrayList<City> handleCity(String data){
        Gson gson=new Gson();
        return gson.fromJson(data,new TypeToken<ArrayList<City>>(){}.getType());
    }

}
