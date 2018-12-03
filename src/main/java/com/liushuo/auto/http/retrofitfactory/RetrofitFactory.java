package com.liushuo.auto.http.retrofitfactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public interface RetrofitFactory {
    Retrofit createRetrofit();
    
    String defaultBaseUrl();
    
    OkHttpClient okHttpClient();
    
    Interceptor httpHeaderConfig();
}
