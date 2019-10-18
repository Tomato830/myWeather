package com.tomato830.myweather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato830.myweather.location.City;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ArrayList<City> citys=new ArrayList<>();
    Button b;
    TextView t;
    City c=new City();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b=(Button) findViewById(R.id.btn);
        t=(TextView) findViewById(R.id.textv);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                City.search("beij",MainActivity.this);
                SharedPreferences sp=getSharedPreferences("data", MainActivity.MODE_PRIVATE);
                String data=sp.getString("json","123");
                t.setText(data);
            }
        });
    }


}
