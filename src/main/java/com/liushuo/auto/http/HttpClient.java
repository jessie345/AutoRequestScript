package com.liushuo.auto.http;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HttpClient {
    private static final Map<OkHttpClient, Map<Class, HttpService>> sServiceCache = new HashMap<>();
    
    static {
        RetrofitManager.setDefaultBaseUrl("http://www.baidu.com");
    }
    
    @Nullable
    public static JsonObject executeHttpCall(@NonNull Call<JsonElement> call) {
        Preconditions.checkNotNull(call);
        
        try {
            Response<JsonElement> response = call.execute();
            return (JsonObject) response.body();
        } catch (IOException e) {
            e.printStackTrace();
            
            return null;
        }
        
    }
    
    @NonNull
    public static HttpService getApiService() {
        
        OkHttpClient okHttpClient = makeOkHttpClient();
        
        synchronized (sServiceCache) {
            if (sServiceCache.get(okHttpClient) == null) {
                sServiceCache.put(okHttpClient, Maps.<Class, HttpService>newHashMap());
            }
            
            Map<Class, HttpService> serviceMap = sServiceCache.get(okHttpClient);
            if (serviceMap.get(HttpService.class) == null) {
                Retrofit retrofit = RetrofitManager.getRetrofit(okHttpClient);
                serviceMap.put(HttpService.class, retrofit.create(HttpService.class));
            }
            
            return sServiceCache.get(okHttpClient).get(HttpService.class);
        }
    }
    
    
    @NonNull
    private static OkHttpClient makeOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                                                           .connectTimeout(10, TimeUnit.SECONDS)
                                                           .readTimeout(10, TimeUnit.SECONDS)
                                                           .writeTimeout(20, TimeUnit.SECONDS)
                                                           .followRedirects(true)
                                                           .followSslRedirects(true)
                                                           .retryOnConnectionFailure(true)
                                                           .addInterceptor(new SetHeaderInterceptor());
        
        return okHttpClientBuilder.build();
    }
}
