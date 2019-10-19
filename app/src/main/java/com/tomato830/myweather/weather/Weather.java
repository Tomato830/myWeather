package com.tomato830.myweather.weather;

import java.util.ArrayList;

public class Weather {
    //基本参数
    public Basic basic;
    public Update update;
    public String status;
    //请求天气类型
    public Now now;
    public ArrayList<Hourly> hourlyArrayList;
    public ArrayList<Forecast> forecastArrayList;
}
