package com.example.r1.alermapp;

import android.app.KeyguardManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class AlarmActivity extends AppCompatActivity {

    private final static String TAG = AlarmActivity.class.getSimpleName();

    private final int FLAG_DISMISS_KEYGUARD =
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG,"create AlarmActivity");

    }

    @Override
    protected void onResume() {
        getWindow().addFlags(FLAG_DISMISS_KEYGUARD);
        super.onResume();
    }

    @Override
    protected void onStop() {
        getWindow().clearFlags(FLAG_DISMISS_KEYGUARD);
        super.onStop();
    }
}