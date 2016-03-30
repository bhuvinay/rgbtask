package com.example.demo.rgptaskapp.networks;


import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by vinaypratap on 30/3/16.
 */
public interface RequestBuilder {
    @GET("/")
    public void getData(Callback<String> response);
}
