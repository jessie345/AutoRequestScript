package com.liushuo.auto.http;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitManager {
    
    private static final Map<OkHttpClient, Retrofit> sRetrofits = Maps.newHashMap();
    private static String sDefaultBaseUrl;
    
    private RetrofitManager() {
    }
    
    public static void setDefaultBaseUrl(@NonNull String baseUrl) {
        Preconditions.checkNotNull(baseUrl);
        
        sDefaultBaseUrl = baseUrl;
    }
    
    @NonNull
    public static Retrofit getRetrofit(@NonNull OkHttpClient okHttpClient) {
        Preconditions.checkNotNull(okHttpClient);
        Preconditions.checkNotNull(sDefaultBaseUrl != null, "网络库需要指定使用的默认域名!");
        
        if (sRetrofits.get(okHttpClient) == null) {
            synchronized (sRetrofits) {
                if (sRetrofits.get(okHttpClient) == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(sDefaultBaseUrl)
                                                .client(okHttpClient)
                                                .addConverterFactory(ScalarsConverterFactory.create())
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                    
                    sRetrofits.put(okHttpClient, retrofit);
                }
            }
            
        }
        
        return sRetrofits.get(okHttpClient);
    }
}
