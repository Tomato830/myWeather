package com.tomato830.myweather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.Menu;
import android.view.MenuItem;

import com.tomato830.myweather.location.City;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    protected static final int SEARCH=1;

    ArrayList<City> citys=new ArrayList<>();

    Toolbar toolbar ;

    City c=new City();

    //主线程的消息处理器handle
    private Handler handler=new Handler(){
        public void handleMessage(Message msg){

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
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
                startActivity(intent);
                break;
        }
        return true;
    }



}
