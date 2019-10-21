package com.tomato830.myweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tomato830.myweather.HTTP.Http;
import com.tomato830.myweather.location.City;
import com.tomato830.myweather.weather.Weather;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    protected static final int SEARCH=1;
    protected static final int SEARCHCID=2;

    ArrayList<City> citys=new ArrayList<>();

    Toolbar toolbar ;

    TextView textView;

    City c=new City();

    protected static final int GETSEARCHCITYSUCCEES=1;
    protected static final int GETSEARCHCITYERROR=2;
    //主线程的消息处理器handle
    @SuppressLint("HandlerLeak")
    private final Handler handler=new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case GETSEARCHCITYSUCCEES:
                    //SharedPreferences sp=getSharedPreferences("weather",MODE_PRIVATE);
                    //String data=sp.getString("weather","123456");
                    //textView.setText(data);

                    //存有now的weather
                    SharedPreferences sp=getSharedPreferences("weather",MODE_PRIVATE);
                    String json=sp.getString("weather-now","");
                    Weather weather=handleWeatherResponse(json);
                    textView.setText(weather.basic.location+'-'+weather.now.cond_txt+'-'+weather.now.tmp+"°C"+"-体感温度:"+weather.now.fl+"°C");
                    textView.setTextSize(20);
                    break;
                case GETSEARCHCITYERROR:
                    Toast.makeText(MainActivity.this,"加载天气失败,请重试",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
        textView=(TextView) findViewById(R.id.tv);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
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
                    String cid=data.getStringExtra("searchCid");
                    searchWeather(cid,"now",MainActivity.this);

                    break;
            }
        }
    }

    public void searchWeather(String searchCid, final String weatherType, final Context context){
        String path="https://free-api.heweather.net/s6/weather/"+weatherType+'?'+"location="+searchCid+"&key=d2f5e8db4d094001b2662559e0d6539c";

        Http.httpRequest(path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                Toast.makeText(context,"加载天气失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json =response.body().string();

                Weather weather=handleWeatherResponse(json);


                if (weather!=null){
                    SharedPreferences sp=context.getSharedPreferences("weather",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("weather"+'-'+weatherType,json);
                    editor.apply();

                    Message msg=new Message();
                    msg.what=GETSEARCHCITYSUCCEES;
                    handler.sendMessage(msg);
                } else {
                    Message msg_error=new Message();
                    msg_error.what=GETSEARCHCITYERROR;
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
}
