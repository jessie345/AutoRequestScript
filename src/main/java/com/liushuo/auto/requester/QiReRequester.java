package com.liushuo.auto.requester;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liushuo.auto.http.HttpClient;
import com.liushuo.auto.log.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;

public class QiReRequester extends Requester {
    public static void main(String[] args) {
        QiReRequester requester = new QiReRequester();
        requester.autoFetchAllComics();
    }
    
    @Override
    public String mainBaseUrl() {
        return "https://api.qiremanhua.com/";
    }
    
    public void autoFetchAllComics() {
        int page = 1;
        
        Map<String, String> options = new HashMap<>();
        options.put("book_type", "all");
        options.put("sort_type", "all");
        options.put("fee_type", "all");
        options.put("page", page + "");
        options.put("finish_state", "all");
        options.put("timeStamp", "1543801349");
        options.put("apiSign", "7811D9613B50608A324B8BC7AC966B98");
        
        Call<JsonElement> allComicsCall = getHttpService().RetrofitGetApi("https://api.qiremanhua.com/v4/book/list_get", options);
        JsonObject allComicsResult = HttpClient.executeHttpCall(allComicsCall);
        Log.log(allComicsResult.toString());
    }
}
