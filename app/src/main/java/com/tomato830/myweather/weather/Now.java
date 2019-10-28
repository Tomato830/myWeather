package com.tomato830.myweather.weather;

public class Now {
    //均默认摄氏度
    //体感温度
    public int fl;
    //温度
    public int tmp;
    //天气状况代码
    public String cond_code;
    //天气描述
    public String cond_txt;

    @Override
    public String toString() {
        return "Now{" +
                "fl=" + fl +
                ", tmp=" + tmp +
                ", cond_code='" + cond_code + '\'' +
                ", cond_txt='" + cond_txt + '\'' +
                '}';
    }
}
