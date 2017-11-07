package com.example.r1.alermapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class OnBootRecevier2 extends BroadcastReceiver {

    private static final String TAG = OnBootRecevier2.class.getCanonicalName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"OnBoot2 Receive");
        Intent i = new Intent(context.getApplicationContext(), HttpGetService.class);
        context.startService(i);
    }
}
