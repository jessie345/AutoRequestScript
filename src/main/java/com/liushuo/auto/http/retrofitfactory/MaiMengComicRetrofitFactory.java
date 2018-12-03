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

public class MaiMengComicRetrofitFactory implements RetrofitFactory {
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
        return "https://api-app.maimengjun.com/";
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
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Content-Encoding", "gzip")
                    .addHeader("clientversion", "4.3.8")
                    .addHeader("devicetype", "3")
                    .addHeader("devicetoken", "fc251a7df1a4bc3b65562a9e8764b53a")
                    .addHeader("accesstoken", "lfeP0abDMWMFUwsa7vF79w61dl40D+zXTeFlqBgOOyc=")
                    .addHeader("qudao", "yingyongbao")
                    .addHeader("deviceinfo", "android")
                    .addHeader("host", "api-app.maimengjun.com")
                    .addHeader("clientid", "503f6ffb-350a-3d49-88df-f5712f222852");
            
            return requestBuilder.build();
        }
        
    }
}
