package com.tomato830.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tomato830.myweather.HTTP.Http;
import com.tomato830.myweather.location.City;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    EditText search_input;

    Button search_btn;

    TextView city_hint;

    ListView cityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //组件初始化
        search_input=(EditText) findViewById(R.id.search_input);
        search_btn=(Button) findViewById(R.id.search_btn);
        cityList=(ListView) findViewById(R.id.dispCities);
        city_hint=(TextView) findViewById(R.id.city_hint);

        //获取热门城市
        getTopCity(this);
        SharedPreferences sp=getSharedPreferences("searchCities",MODE_PRIVATE);
        //ArrayList<City> cities=handleCityResponse(sp.getString("topcities",""));
        int num=handleCityResponse(sp.getString("topCities",""));
        Log.v(Integer.toString(num),"cities个数");

        //设置ListView的适配器
        //ArrayAdapter<City> adapter=new ArrayAdapter<City>(this,R.layout.listview,cities);
        //cityList.setAdapter(adapter);

        //设置监听器
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_hint.setText("   搜索结果:");
                searchCity(search_input.getText().toString(),SearchActivity.this);

            }
        });
    }

    //搜索城市
    public static void searchCity(String s, final Context context) {
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

    public static int handleCityResponse(String response){
        Gson gson=new Gson();
        ArrayList<City> cities=new ArrayList<>();
        try {
            cities = gson.fromJson(response,new TypeToken<ArrayList<City>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cities.size();
    }
}
