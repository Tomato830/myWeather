package com.tomato830.myweather.location;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;

import com.tomato830.myweather.HTTP.http;
import com.tomato830.myweather.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class City {
    //城市名称
    private String city;
    //城市ID
    private String cid;
    //上级城市
    private String parent_city;
    //所属行政区域
    private String admin_area;
    //所属国家
    private String cnty;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getParent_city() {
        return parent_city;
    }

    public void setParent_city(String parent_city) {
        this.parent_city = parent_city;
    }

    public String getAdmin_area() {
        return admin_area;
    }

    public void setAdmin_area(String admin_area) {
        this.admin_area = admin_area;
    }

    public String getCnty() {
        return cnty;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }


    //搜索城市
    public static ArrayList<City> search(String s,final Context context){
        ArrayList<City> cities=new ArrayList<>();
        String key="d2f5e8db4d094001b2662559e0d6539c";
        try {
            String path = "https://search.heweather.net/find?"+"location="+URLEncoder.encode(s,"utf-8")+"&key="+key;

            http.httpRequest(path, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String json=response.body().string();
                    SharedPreferences sp=context.getSharedPreferences("data",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor= sp.edit();
                    editor.putString("json",json);
                    editor.apply();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return cities;
    }
    //获取热门搜索城市
    public static ArrayList<City> getTopCity(){
        ArrayList<City> cities=new ArrayList<>();


        return cities;
    }


}
