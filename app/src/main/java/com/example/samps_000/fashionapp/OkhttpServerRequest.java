package com.example.samps_000.fashionapp;

import android.content.Context;
import android.util.Log;

import com.stormpath.sdk.Stormpath;
import com.stormpath.sdk.utils.StringUtils;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by samps_000 on 2/23/2016.
 */
public class OkhttpServerRequest{

    RequestBody body = null;
    Context context = null;
    String ext = null;
    OkHttpClient okHttpClient = null;

    public OkhttpServerRequest(Context con, String url_ext, RequestBody... params) {
        Log.d("checks", "checking for body " + params);
        if (params.length >= 1){
            Log.d("checks", "setting body " + params[0]);
            body = params[0];
        }

        context = con;
        ext = url_ext;

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(httpLoggingInterceptor).build();
    }

    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
            Stormpath.logger().d(message);
        }
    });

    public Request getRequest(){
        Request request;

        if (body != null) {
            Log.d("checks", "body is not null returning post " + body);
            request = new Request.Builder()
                    .url(context.getString(R.string.SERVER_URL) + ext)
                    .headers(buildStandardHeaders(Stormpath.accessToken()))
                    .post(body)
                    .build();
        }else {
            Log.d("checks", "body is null returning get " + body);
            request = new Request.Builder()
                    .url(context.getString(R.string.SERVER_URL) + ext)
                    .headers(buildStandardHeaders(Stormpath.accessToken()))
                    .get()
                    .build();
        }
        return request;
    }

    public OkHttpClient getClient(){
        return okHttpClient;
    }

    private Headers buildStandardHeaders(String accessToken) {
        Headers.Builder builder = new Headers.Builder();
        builder.add("Accept", "application/json");
        if (StringUtils.isNotBlank(accessToken)) {
            builder.add("Authorization", "Bearer " + accessToken);
        }

        return builder.build();
    }
}
