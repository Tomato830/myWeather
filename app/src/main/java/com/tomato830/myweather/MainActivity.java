package com.tomato830.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato830.myweather.location.City;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    protected static final int SEARCH=1;
    ArrayList<City> citys=new ArrayList<>();
    Button b;
    EditText search;
    TextView res;
    City c=new City();
    //主线程的消息处理器handle
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            if (msg.what==SEARCH){
                String data=(String) msg.obj;
                res.setText(data);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b=(Button) findViewById(R.id.btn);
        search=(EditText) findViewById(R.id.search_city);
        res=(TextView) findViewById(R.id.res);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s=search.getText().toString();
                City.search(s,MainActivity.this);
                //将搜索结果从文件中取出,耗时操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sp=getSharedPreferences("searchCities", MainActivity.MODE_PRIVATE);
                        String data=sp.getString("searchCities","");
                        Message msg=new Message();
                        msg.what=SEARCH;
                        msg.obj=data;
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }


}
