package com.example.r1.alermapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
    //Broadcastが何度も飛んでくるので最後の一度だけ処理する
    public static int gCounter = 0;

    private static final String TAG = WifiReceiver.class.getSimpleName();

    private Handler mHandler = new Handler();
    private int mState = 0;
    private Runnable mRunTask = new Runnable() {
        @Override
        public void run() {
            //最後の一回だけ処理
            if (gCounter == 1) {
                HttpGetService.updateWifiState(mState);
                gCounter =0;
            }
            else {
                gCounter--;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) { // "android.net.wifi.STATE_CHANGE"
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState()== NetworkInfo.State.CONNECTED) {
                // Wifi is connected
                gCounter++;
                mState = 1;
                Log.d("Inetify", "Wifi is connected: " + String.valueOf(info));
                mHandler.postDelayed(mRunTask,5000);
            }
            if (info.getState()== NetworkInfo.State.DISCONNECTED) {
                gCounter++;
                mState=0;
                Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(info));
                mHandler.postDelayed(mRunTask,5000);
            }
        }
    }
}
