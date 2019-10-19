package com.tomato830.myweather.location;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.tomato830.myweather.HTTP.Http;
import com.tomato830.myweather.weather.Basic;

import org.jetbrains.annotations.NotNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Response;

public class City {
    public Basic basic;
    public String status;

    //搜索城市
    public static void search(String s, final Context context) {
        String path = null;
        try {
            path = "https://search.heweather.net/find?" + "location=" + URLEncoder.encode(s, "utf-8")
                    + "&key=d2f5e8db4d094001b2662559e0d6539c";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //访问搜索API,将结果存入searchCities文件
        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "搜索城市失败,请重试", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    //得到basic数组和status
                    JSONObject jsonObject = new JSONObject(json).getJSONArray("HeWeather6").getJSONObject(0);
                    //status=ok,将basic存入文件
                    if ("ok".equals(jsonObject.optString("status"))) {
                        SharedPreferences sp = context.getSharedPreferences("searchCities", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("searchCities", jsonObject.optString("basic"));
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //获取热门搜索城市
    public static void getTopCity(final Context context) {
        String path = "https://search.heweather.net/top?" + "group=world"
                + "&key=d2f5e8db4d094001b2662559e0d6539c";
        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "获取热门城市失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    //得到basic数组和status
                    JSONObject jsonObject = new JSONObject(json).getJSONArray("HeWeather6").getJSONObject(0);
                    //status=ok,将basic存入文件
                    if ("ok".equals(jsonObject.optString("status"))) {
                        SharedPreferences sp = context.getSharedPreferences("topCities", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("topCities", jsonObject.optString("basic"));
                        editor.apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
