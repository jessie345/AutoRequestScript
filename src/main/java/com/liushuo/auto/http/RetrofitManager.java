package com.liushuo.auto.http;

import com.google.common.base.Preconditions;
import com.liushuo.auto.http.retrofitfactory.MaiMengComicRetrofitFactory;
import com.liushuo.auto.http.retrofitfactory.QiReComicRetrofitFactory;
import com.liushuo.auto.http.retrofitfactory.RetrofitFactory;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;

public class RetrofitManager {
    
    private static Map<String, RetrofitFactory> sRetrofitFactories = new HashMap<>();
    private static Map<String, Retrofit> sRetrofits = new HashMap<>();
    
    static {
        // 麦萌漫画
        RetrofitFactory maiMengRF = new MaiMengComicRetrofitFactory();
        sRetrofitFactories.put(maiMengRF.defaultBaseUrl(), maiMengRF);
        
        // 奇热漫画
        RetrofitFactory qiReRF = new QiReComicRetrofitFactory();
        sRetrofitFactories.put(qiReRF.defaultBaseUrl(), qiReRF);
    }
    
    private RetrofitManager() {
    }
    
    @NonNull
    public static Retrofit getRetrofit(@NonNull String baseUrl) {
        Preconditions.checkNotNull(baseUrl);
        
        if (sRetrofits.get(baseUrl) == null) {
            synchronized (sRetrofits) {
                if (sRetrofits.get(baseUrl) == null) {
                    
                    RetrofitFactory retrofitFactory = sRetrofitFactories.get(baseUrl);
                    if (retrofitFactory == null) {
                        throw new RuntimeException("没有找到指定的Http Retrofit factory for url:" + baseUrl);
                    }
                    
                    Retrofit retrofit = retrofitFactory.createRetrofit();
                    sRetrofits.put(baseUrl, retrofit);
                }
            }
            
        }
        
        return sRetrofits.get(baseUrl);
    }
}
