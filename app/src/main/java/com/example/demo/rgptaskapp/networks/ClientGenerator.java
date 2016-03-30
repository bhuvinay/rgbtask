package com.example.demo.rgptaskapp.networks;

import android.util.Log;

import com.example.demo.rgptaskapp.utils.Constants;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.Profiler;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

/**
 * Created by vinaypratap on 30/3/16.
 */
public class ClientGenerator {


    private static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(Constants.API_BASE_URL)
            .setLog(new AndroidLog("Vinay"))
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(getOkClient());

    public static <S> S createService(Class<S> serviceClass) {
        builder.setProfiler(new Profiler() {
            @Override
            public Object beforeCall() {
                return null;
            }

            @Override
            public void afterCall(RequestInformation requestInfo, long elapsedTime, int statusCode, Object beforeCallData) {
                Log.d("Retrofit Profiler", String.format("HTTP %d %s %s (%dms)",
                        statusCode, requestInfo.getMethod(), requestInfo.getRelativePath(), elapsedTime));
            }
        });
        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }

    public static OkClient getOkClient() {
        OkHttpClient client = new OkHttpClient();
        //timeout set to 1 sec
        client.setConnectTimeout(1000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(1000, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(1000, TimeUnit.MILLISECONDS);
        OkClient _client = new OkClient(client);
        return _client;
    }
}
