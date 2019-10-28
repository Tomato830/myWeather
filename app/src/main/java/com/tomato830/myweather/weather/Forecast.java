package com.tomato830.myweather.weather;

public class Forecast {
    //逐天预报
    //预报日期
    public String date;
    //最高温
    public int tmp_max;
    //最低温
    public int tmp_min;
    //白天天气描述
    public String cond_txt_d;

    @Override
    public String toString() {
        return "Forecast{" +
                "date='" + date + '\'' +
                ", tmp_max=" + tmp_max +
                ", tmp_min=" + tmp_min +
                ", cond_txt_d='" + cond_txt_d + '\'' +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTmp_max() {
        return tmp_max;
    }

    public void setTmp_max(int tmp_max) {
        this.tmp_max = tmp_max;
    }

    public int getTmp_min() {
        return tmp_min;
    }

    public void setTmp_min(int tmp_min) {
        this.tmp_min = tmp_min;
    }

    public String getCond_txt_d() {
        return cond_txt_d;
    }

    public void setCond_txt_d(String cond_txt_d) {
        this.cond_txt_d = cond_txt_d;
    }
}
