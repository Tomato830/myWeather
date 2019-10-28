package com.tomato830.myweather.weather;

public class Lifestyle {
    public String type;//生活指数类型
    public String brf;//生活指数简述
    public String txt;//生活指数详述

    @Override
    public String toString() {
        return "Lifestyle{" +
                "type='" + type + '\'' +
                ", brf='" + brf + '\'' +
                ", txt='" + txt + '\'' +
                '}';
    }
}
