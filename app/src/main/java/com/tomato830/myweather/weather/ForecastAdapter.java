package com.tomato830.myweather.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.tomato830.myweather.R;

import java.util.ArrayList;

public class ForecastAdapter extends BaseAdapter {

    TextView date;

    TextView txt;

    TextView tmp_max;

    TextView tmp_min;

    ArrayList<Forecast> forecasts;

    Context context;

    LayoutInflater layoutInflater;

    public ForecastAdapter(Context context,ArrayList<Forecast> forecasts) {
        this.context=context;
        this.forecasts=forecasts;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return forecasts.size();
    }

    @Override
    public Object getItem(int position) {
        return forecasts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.forecast_list,parent,false);
        }
        Forecast forecast=(Forecast) getItem(position);

        date=(TextView) convertView.findViewById(R.id.date_text);
        txt=(TextView) convertView.findViewById(R.id.info_text);
        tmp_max=(TextView) convertView.findViewById(R.id.max_text);
        tmp_min=(TextView) convertView.findViewById(R.id.min_text);

        date.setText(forecast.date);
        txt.setText(forecast.cond_txt_d);
        tmp_max.setText("最高温度:\n"+Integer.toString(forecast.tmp_max)+"°C");
        tmp_min.setText("最低温度:\n"+Integer.toString(forecast.tmp_min)+"°C");

        return convertView;
    }
}
