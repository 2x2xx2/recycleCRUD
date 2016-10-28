package com.dinkominfo.recyclecrud.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestGet {
    Context context;
    String url,jsonData="";
    public RequestGet(String url, Context context) {
        this.url=url;
        this.context=context;
    }
    public void execGet(Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        try{
            Request newReq = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(newReq).enqueue(callback);
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
    }
}
