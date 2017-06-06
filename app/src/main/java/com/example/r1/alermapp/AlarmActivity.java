package com.example.r1.alermapp;

import android.app.KeyguardManager;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class AlarmActivity extends AppCompatActivity {

    private final static String TAG = AlarmActivity.class.getSimpleName();

    private final int FLAG_DISMISS_KEYGUARD =
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;

    Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Log.d(TAG,"create AlarmActivity");

        findViewById(R.id.gotohome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ringtone.stop();
                //ホーム画面に戻るインテントを起動
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                AlarmActivity.this.startActivity(homeIntent);
                finish();
            }
        });


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);     // 着信音
        ringtone = RingtoneManager.getRingtone(this, uri);

        ringtone.play(); // 再生

    }



    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onAttachedToWindow() {

    }
}
