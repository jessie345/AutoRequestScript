package com.liushuo.auto.requester;

import com.liushuo.auto.http.HttpClient;
import com.liushuo.auto.http.HttpService;

public abstract class Requester {
    private HttpService mHttpService;
    
    public Requester() {
        mHttpService = HttpClient.getApiService(mainBaseUrl());
    }
    
    public final HttpService getHttpService() {
        return mHttpService;
    }
    
    abstract String mainBaseUrl();
}
