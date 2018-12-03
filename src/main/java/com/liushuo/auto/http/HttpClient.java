package com.liushuo.auto.http;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HttpClient {
    private static final Map<String, HttpService> sServiceCache = new HashMap<>();
    
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
    public static HttpService getApiService(@NonNull String baseUrl) {
        if (sServiceCache.get(baseUrl) == null) {
            synchronized (sServiceCache) {
                if (sServiceCache.get(baseUrl) == null) {
                    Retrofit retrofit = RetrofitManager.getRetrofit(baseUrl);
                    sServiceCache.put(baseUrl, retrofit.create(HttpService.class));
                }
                
            }
        }
        return sServiceCache.get(baseUrl);
    }
    
}
