package com.example.r1.alermapp.util;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
/*
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;


public abstract class MainThreadCallback implements Callback {
    private static final String TAG = MainThreadCallback.class.getSimpleName();

    abstract public void onFail(final Exception error);

    abstract public void onSuccess(final JSONObject responseBody);


    public void onFailure(final Request request, final IOException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                e.printStackTrace();
                onFail(e);
            }
        });
    }

    public void onResponse(final Response response) throws IOException {
        if (!response.isSuccessful() || response.body() == null) {
            onFailure(response.request(), new IOException("Failed"));
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject responseBody = new JSONObject();
                    responseBody.put("body",response.body().string());
                    responseBody.put("code",response.body());
                    onSuccess(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(response.request(), new IOException("Failed"));
                }catch  (JSONException e) {
                    e.printStackTrace();
                    onFailure(response.request(), new IOException("JsonConvert Failed"));
                }
            }
        });
    }

    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }
}
*/