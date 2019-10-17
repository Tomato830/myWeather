package com.tomato830.myweather.location;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

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
    public static ArrayList<City> search(String s){
        String key="18326950229a403db7ceab82de428237";
        String path="https://search.heweather.net/find?"+"key="+key+'&'+"location="+ URLEncoder.encode(s)+'&'+"mode="+"equal";
        URL url= null;
        try {
            url = new URL(path);
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int responseCode=connection.getResponseCode();
            if (responseCode==200){
                InputStream is=connection.getInputStream();
            }
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<City> c =new ArrayList<>();


        return c;
    }
    //获取热门搜索城市
    public static ArrayList<City> getTopCity(){
        ArrayList<City> c=new ArrayList<>();


        return c;
    }
}
