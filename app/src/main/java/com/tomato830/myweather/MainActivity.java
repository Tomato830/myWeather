package com.tomato830.myweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tomato830.myweather.HTTP.Http;
import com.tomato830.myweather.location.City;
import com.tomato830.myweather.weather.Forecast;
import com.tomato830.myweather.weather.ForecastAdapter;
import com.tomato830.myweather.weather.Hourly;
import com.tomato830.myweather.weather.Lifestyle;
import com.tomato830.myweather.weather.Weather;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import android.net.Uri;
import java.net.URISyntaxException;

import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    protected static final int SEARCH=1;
    protected static final int SEARCHCID=2;

    Toolbar toolbar ;

    //now布局
    ImageView now_imageView;

    TextView now_cond_txt;

    TextView now_tmp;

    //forecast布局
    ListView listView;

    //fragment布局
    ImageView weather_bg;

    //lifestyle布局
    TextView comfort_txt;

    TextView dress_txt;

    TextView sport_txt;

    TextView spi_txt;//防晒指数
    //默认cid
    String cid="auto_ip";

    SwipeRefreshLayout swipeRefreshLayout;

    protected static final int SEARCHWEATHERSUCCEES=1;//搜索天气成功(即三种类型)
    protected static final int NOWWEARTHERERROR=2;//当前天气加载失败
    protected static final int LIFESTYLEWEATHERERROR=3;//小时预报加载失败
    protected static final int FORECASTWEATHERERROR=4;//天预报加载失败
    //主线程的消息处理器handle
    @SuppressLint("HandlerLeak")
    private final Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case SEARCHWEATHERSUCCEES:
                    //存有now的weather
                    SharedPreferences sp=getSharedPreferences("weather",MODE_PRIVATE);
                    String json=sp.getString("weather-now","");
                    Weather weather=handleWeatherResponse(json);

                    //存有hourly的weather
                    json=sp.getString("weather-hourly","");
                    Weather weather_hourly=handleWeatherResponse(json);

                    //存有forecast的weather
                    json=sp.getString("weather-forecast","");
                    Weather weather_forecast=handleWeatherResponse(json);

                    //存有lifestyle的weather
                    json=sp.getString("weather-lifestyle","");
                    Weather weather_lifestyle=handleWeatherResponse(json);

                    //存入同一个weather
                    //.clone()深拷贝
                    weather.daily_forecast=(ArrayList<Forecast>) weather_forecast.daily_forecast.clone();
                    weather.lifestyle=(ArrayList<Lifestyle>) weather_lifestyle.lifestyle.clone();
                    break;
                case NOWWEARTHERERROR:
                    Toast.makeText(MainActivity.this,"加载当前天气失败,请重试",Toast.LENGTH_SHORT).show();
                    break;
                case FORECASTWEATHERERROR:
                    Toast.makeText(MainActivity.this,"加载周天气预报失败,请重试",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        weather_bg = (ImageView) findViewById(R.id.weather_bg);
        now_imageView = (ImageView) findViewById(R.id.cond_pic);
        now_cond_txt = (TextView) findViewById(R.id.cond_txt);
        now_tmp = (TextView) findViewById(R.id.now_tmp);
        listView = (ListView) findViewById(R.id.forecast_listview);
        comfort_txt=(TextView) findViewById(R.id.comfort_text);
        dress_txt=(TextView) findViewById(R.id.dress_text);
        sport_txt=(TextView) findViewById(R.id.sport_text);
        spi_txt=(TextView) findViewById(R.id.spi_text);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String[] weatherType={"now","forecast","lifestyle"};
                for (int i=0;i<weatherType.length;++i){
                    searchWeather(cid,weatherType[i],MainActivity.this);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        init();
    }

    @Override
    //设置菜单布局
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    //设置菜单按钮
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_city_menu:
                //启用搜索Activity
                Intent intent =new Intent(this,SearchActivity.class);
                startActivityForResult(intent,SEARCHCID);
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null){
            switch (requestCode){
                case SEARCHCID:

                    Toast.makeText(MainActivity.this,"从搜索界面返回,搜索中...",Toast.LENGTH_SHORT).show();

                    //从SearchActivity返回
                    cid=data.getStringExtra("searchCid");
                    //hourly没有免费的API了
                    String[] weatherType={"now","forecast","lifestyle"};
                    for (int i=0;i<weatherType.length;++i){
                        searchWeather(cid,weatherType[i],MainActivity.this);
                    }
                    break;
            }
        }
    }

    public void searchWeather(String searchCid, final String weatherType, final Context context){
        final String path="https://free-api.heweather.net/s6/weather/"+weatherType+'?'+"location="+searchCid+"&key=d2f5e8db4d094001b2662559e0d6539c";

        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"加载天气失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull Response response) throws IOException {
                String json =response.body().string();

                final Weather weather=handleWeatherResponse(json);

                if (weather!=null){
                    //解析成功,存入weather文件
                    SharedPreferences sp=context.getSharedPreferences("weather",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("weather"+'-'+weatherType,json);
                    Log.v("请求天气类型",weatherType);
                    editor.apply();

                    if (weatherType.equals("now")){
                        //加载天气图片
                        String pic=weather.now.cond_code;
                        URL url=new URL("https://cdn.heweather.com/cond_icon/"+pic+".png");

                    }
                    //修改ui
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            //
                            //
                            switch (weatherType){
                                case "now":
                                    //设置toolbar标题
                                    toolbar.setTitle(weather.basic.location);
                                    //设置图片
                                    String picPath="https://cdn.heweather.com/cond_icon/"+weather.now.cond_code+".png";
                                    //loadPic(picPath,"weather_cond");
                                    GlideApp.with(MainActivity.this).load(picPath).error(R.drawable.p999).override(400,400).into(now_imageView);
                                    //设置描述
                                    now_cond_txt.setText(weather.now.cond_txt);
                                    //设置温度
                                    String tmp="目前温度:"+Integer.toString(weather.now.tmp)+"°C\n"+"体感温度:"+Integer.toString(weather.now.fl)+"°C";
                                    Log.v("tmp",tmp);
                                    now_tmp.setText(tmp);
                                    break;
                                case "forecast":
                                    listView.setAdapter(new ForecastAdapter(MainActivity.this,weather.daily_forecast));
                                    break;
                                case "lifestyle":
                                    String[] txt=new String[4];
                                    Lifestyle lifestyle=null;
                                    for (int i=0;i<weather.lifestyle.size();++i){
                                        lifestyle=weather.lifestyle.get(i);
                                        switch (lifestyle.type){
                                            case "comf":
                                                txt[0]="舒适度指数:"+lifestyle.brf+'\n'+lifestyle.txt;
                                                break;
                                            case "drsg":
                                                txt[1]="穿衣指数:"+lifestyle.brf+'\n'+lifestyle.txt;
                                                break;
                                            case "sport":
                                                txt[2]="运动指数:"+lifestyle.brf+'\n'+lifestyle.txt;
                                                break;
                                            case "air":
                                                txt[3]="空气指数:"+lifestyle.brf+'\n'+lifestyle.txt;
                                                break;
                                        }
                                    }
                                    comfort_txt.setText(txt[0]);
                                    dress_txt.setText(txt[1]);
                                    sport_txt.setText(txt[2]);
                                    spi_txt.setText(txt[3]);
                                    break;
                            }
                        }
                    });


                } else {
                    //weather解析失败
                    Message msg_error=new Message();
                    switch (weatherType){
                        case "now":
                            msg_error.what=NOWWEARTHERERROR;
                            break;
                        case "lifestyle":
                            msg_error.what=LIFESTYLEWEATHERERROR;
                            break;
                        case "forecast":
                            msg_error.what=FORECASTWEATHERERROR;
                            break;
                    }
                    handler.sendMessage(msg_error);
                }

            }
        });
    }

    public Weather handleWeatherResponse(String response){
        Gson gson=new Gson();

        try {
            //解析json
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            jsonObject=jsonArray.getJSONObject(0);

            if (!"ok".equals(jsonObject.optString("status"))){
                return null;
            }
            response=jsonObject.toString();

            //gson解析
            Weather weather=gson.fromJson(response,Weather.class);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getBingPic(){
        String path="https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        //返回json,解析等到url
        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"加载背景图片失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json=response.body().string();

                JSONObject jsonObject= null;
                try {
                    jsonObject = new JSONObject(json);
                    JSONArray jsonArray=jsonObject.getJSONArray("images");
                    jsonObject=(JSONObject) jsonArray.get(0);
                    String url=jsonObject.optString("url");
                    Log.v("url",url);

                    if (url!=null){
                        final String picUrl="https://cn.bing.com"+url;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GlideApp.with(MainActivity.this).load(picUrl).error(R.drawable.th).into(weather_bg);
                            }
                        });
                    }else {
                        Toast.makeText(MainActivity.this,"加载背景图片失败",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    public void init(){
        getBingPic();
        //进入时自动刷新
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                String[] weatherType={"now","forecast","lifestyle"};
                for (int i=0;i<weatherType.length;++i){
                    searchWeather(cid,weatherType[i],MainActivity.this);
                }
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }
}
