package com.tomato830.myweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    //ProgressBar progressBar;

    SwipeRefreshLayout swipeRefreshLayout;

    protected static final int GETTOPCITIESERROR = 1;
    protected static final int GETTOPCITIESSUCCEES=2;
    protected static final int SEARCHCID=2;
    protected static final int GETSEARCHCITIESSUCCEES=3;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETTOPCITIESERROR:
                    //热门城市获取失败
                    Toast.makeText(SearchActivity.this, "获取热门城市失败", Toast.LENGTH_LONG).show();
                    break;
                case GETTOPCITIESSUCCEES:
                    //热门城市获取成功
                    SharedPreferences sp=getSharedPreferences("topCities",MODE_PRIVATE);
                    City city=handleCityResponse(sp.getString("topCities",""));

                    //将City转为String[]
                    final String[] topcities_txt=new String[city.basic.size()];
                    final String[] topcities_cid=new String[city.basic.size()];
                    for (int i=0;i<city.basic.size();++i){
                        topcities_txt[i]=city.basic.get(i).location+'-'+city.basic.get(i).parent_city+','+city.basic.get(i).admin_area;
                        topcities_cid[i]=city.basic.get(i).cid;
                    }

                    ArrayAdapter<String> adapter=new ArrayAdapter<>(SearchActivity.this,R.layout.listview,topcities_txt);
                    cityList.setAdapter(adapter);

                    cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //将点击结果返回上一层
                            Intent intent=new Intent();
                            intent.putExtra("searchCid",topcities_cid[position]);
                            setResult(SEARCHCID,intent);

                            //
                            SearchActivity.this.finish();
                        }
                    });
                    break;
                case GETSEARCHCITIESSUCCEES:
                    //搜索城市成功
                    SharedPreferences sharedPreferences=getSharedPreferences("searchCities",MODE_PRIVATE);
                    City searchCity=handleCityResponse(sharedPreferences.getString("searchCities",""));

                    //将City转为String[]
                    final String[] searchcities_txt=new String[searchCity.basic.size()];
                    final String[] searchcities_cid=new String[searchCity.basic.size()];
                    for (int i=0;i<searchCity.basic.size();++i){
                        searchcities_txt[i]=searchCity.basic.get(i).location+'-'+searchCity.basic.get(i).parent_city+','+searchCity.basic.get(i).admin_area;
                        searchcities_cid[i]=searchCity.basic.get(i).cid;
                    }

                    //搜索完毕,关闭progressBar
                    //progressBar.setVisibility(View.GONE);

                    ArrayAdapter<String> adapter_search=new ArrayAdapter<>(SearchActivity.this,R.layout.listview,searchcities_txt);
                    cityList.setAdapter(adapter_search);

                    Toast.makeText(SearchActivity.this,"返回...",Toast.LENGTH_SHORT).show();

                    cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            //将点击结果返回上一层
                            Intent intent = new Intent();
                            intent.putExtra("searchCid", searchcities_cid[position]);
                            setResult(SEARCHCID, intent);

                            //SearchActivity结束,返回MainActivity
                            SearchActivity.this.finish();
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //组件初始化
        search_input = (EditText) findViewById(R.id.search_input);
        search_btn = (Button) findViewById(R.id.search_btn);
        cityList = (ListView) findViewById(R.id.dispCities);
        city_hint = (TextView) findViewById(R.id.city_hint);

        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        //获取热门城市
        getTopCity(this);

        //设置监听器
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city_hint.setText("搜索结果:");
                searchCity(search_input.getText().toString(), SearchActivity.this);
            }
        });
    }

    //搜索城市
    public void searchCity(String s, final Context context) {

        //搜索中,显示progressBar
        //progressBar.setVisibility(View.VISIBLE);

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
                //返回的完整的json文件
                String json = response.body().string();
                //处理json文件
                City city = handleCityResponse(json);

                //
                if (city != null) {
                    SharedPreferences sp = context.getSharedPreferences("searchCities", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("searchCities", json);
                    editor.apply();

                    Message msg = new Message();
                    msg.what = GETSEARCHCITIESSUCCEES;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    //获取热门搜索城市
    public void getTopCity(final Context context) {
        String path = "https://search.heweather.net/top?" + "group=world"
                + "&key=d2f5e8db4d094001b2662559e0d6539c";
        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "获取热门城市失败", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String json = response.body().string();

                final City city = handleCityResponse(json);

                if (city != null) {
                    SharedPreferences sp = context.getSharedPreferences("topCities", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("topCities", json);
                    editor.apply();

                    Message msg = new Message();
                    msg.what = GETTOPCITIESSUCCEES;
                    handler.sendMessage(msg);
                }
            }
        });
    }


    //处理返回的城市的json文件
    public City handleCityResponse(String response) {
        Gson gson = new Gson();

        try {
            //解析json对象
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            jsonObject = jsonArray.getJSONObject(0);

            if (!"ok".equals(jsonObject.optString("status"))) {
                return null;
            }
            response = jsonObject.toString();

            //gson解析basic数组
            City city = gson.fromJson(response, City.class);
            return city;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //异常退出,跳到这里
        return null;
    }

    private void showCitiesInfo(City[] cities) {




    }
}
