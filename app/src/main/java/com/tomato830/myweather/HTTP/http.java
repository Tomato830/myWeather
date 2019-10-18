package com.tomato830.myweather.HTTP;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class http {
    public static void httpRequest(String path,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request =new Request.Builder()
                .url(path)
                .build();
        client.newCall(request).enqueue(callback);
    }


}
