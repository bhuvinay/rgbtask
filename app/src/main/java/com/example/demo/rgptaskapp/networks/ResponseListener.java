package com.example.demo.rgptaskapp.networks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by vinaypratap on 30/3/16.
 */
public abstract class ResponseListener<T> implements Callback<T> {
    private static List<ResponseListener> requestList = new ArrayList<>();
    private boolean isCancelled = false;
    private Object mTag = null;

    public ResponseListener() {
        requestList.add(this);
    }

    public ResponseListener(Object tag) {
        mTag = tag;
        requestList.add(this);
    }

    public static void cancelAll() {
        Iterator<ResponseListener> iterator = requestList.iterator();
        while (iterator.hasNext()) {
            iterator.next().isCancelled = true;
            iterator.remove();
        }
    }

    public static void cancel(Object tag) {
        if (tag != null) {
            Iterator<ResponseListener> iterator = requestList.iterator();
            ResponseListener item;
            while (iterator.hasNext()) {
                item = iterator.next();
                if (tag.equals(item.mTag)) {
                    item.isCancelled = true;
                    iterator.remove();
                }
            }
        }
    }


    public void cancel() {
        isCancelled = true;
        requestList.remove(this);
    }

    @Override
    public void success(T t, Response response) {
        if (!isCancelled) {
            onSuccess(t);
        }
        requestList.remove(this);
    }


    @Override
    public void failure(RetrofitError error) {
        if (!isCancelled)
            onError(error);
        requestList.remove(this);

    }


    public abstract void onSuccess(T result);


    public abstract void onError(RetrofitError error);



}
