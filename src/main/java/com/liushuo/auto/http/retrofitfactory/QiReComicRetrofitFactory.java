package com.liushuo.auto.http.retrofitfactory;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class QiReComicRetrofitFactory implements RetrofitFactory {
    @Override
    public Retrofit createRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(defaultBaseUrl())
                                    .client(okHttpClient())
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
        return retrofit;
    }
    
    @Override
    public String defaultBaseUrl() {
        return "https://api.qiremanhua.com/";
    }
    
    @Override
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                                                           .connectTimeout(10, TimeUnit.SECONDS)
                                                           .readTimeout(10, TimeUnit.SECONDS)
                                                           .writeTimeout(20, TimeUnit.SECONDS)
                                                           .followRedirects(true)
                                                           .followSslRedirects(true)
                                                           .retryOnConnectionFailure(true)
                                                           .addInterceptor(httpHeaderConfig());
        
        return okHttpClientBuilder.build();
    }
    
    @Override
    public Interceptor httpHeaderConfig() {
        return new HeaderConfigInterceptor();
    }
    
    private static class HeaderConfigInterceptor implements Interceptor {
        
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = configureNewGatewayCommonHeaders(chain.request());
            return chain.proceed(request);
        }
        
        @NonNull
        private Request configureNewGatewayCommonHeaders(@NonNull Request original) {
            Request.Builder requestBuilder = original.newBuilder();
            
            //set auth 2.0 header
            requestBuilder
                    .addHeader("qrmh-version-code", "202")
                    .addHeader("qrmh-version", "2.0.2")
                    .addHeader("qrmh-client", "android")
                    .addHeader("qrmh-token", "V3oPdwBpUDVUcws-BXdTMwFpXWdRP1A4ADVWM1ZgBiVSKAtzVSFXdFZpAT9WMwdiBGcLM1A_AGYANAZuBDkEeA")
                    .addHeader("qrmh-channel", "141404")
                    .addHeader("qrmh-uuid", "866147043601097");
            
            return requestBuilder.build();
        }
        
    }
}
