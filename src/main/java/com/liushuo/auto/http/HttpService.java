package com.liushuo.auto.http;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface HttpService {
    @POST()
    Call<JsonElement> RetrofitPostBodyApi(@Url String url, @Body JsonObject bodyParams, @QueryMap Map<String, String> queryParams);
    
    @FormUrlEncoded
    @POST()
    Call<JsonElement> RetrofitPostFormApi(@Url String url, @FieldMap() Map<String, String> formParam, @QueryMap Map<String, String> queryParams);
    
    @GET()
    Call<JsonElement> RetrofitGetApi(@Url String url, @QueryMap Map<String, String> options);
    
    @PUT()
    Call<JsonElement> RetrofitPutApi(@Url String url, @Body JsonObject param, @QueryMap Map<String, String> options);
    
    @DELETE
    Call<JsonElement> RetrofitSimpleDeleteApi(@Url String url, @QueryMap Map<String, String> options);
    
    @HTTP(method = "DELETE", hasBody = true)
    Call<JsonElement> RetrofitDeleteWithBodyApi(@Url String url, @Body JsonObject param, @QueryMap Map<String, String> options);
    
}
