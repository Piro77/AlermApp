package com.example.r1.alermapp;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 常駐型サービスのサンプル。定期的にログ出力する。
 * @author id:language_and_engineering
 *
 */
public class SamplePeriodicService extends BasePeriodicService
{
    private final static String TAG = SamplePeriodicService.class.getSimpleName();
    // 画面から常駐を解除したい場合のために，常駐インスタンスを保持
    public static BasePeriodicService activeService;


    @Override
    protected long getIntervalMS() {
        //10時から9時までは5分おきその他は一時間
        Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        Log.d(TAG,"Hour "+String.valueOf(hh)+" Min "+String.valueOf(mm));
        if ((hh > 21) || (hh < 9)) {
            return 1000 * 1800;
        }
        return 1000 * 120;
    }


    @Override
    protected void execTask() {
        activeService = this;


        // ※もし毎回の処理が重い場合は，メインスレッドを妨害しないために
        // ここから下を別スレッドで実行する。
        OkHttpClient client = OkHttpSingleton.getInstance().getOkHttpClient();

        final Request request = new Request.Builder()
                .url("http://192.168.1.23/check.html")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失敗しても特に何もしない。
                Log.d(TAG,"fail "+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG,"Success");
                    String body = response.body().string();
                    if (body.contains("NG")) {
                        //応答にNGがあった場合なにかする。
                        Log.d(TAG,"NG Detect");
                        setNotification();
                    }
                    if (body.contains("ALARM")) {
                        Log.d(TAG,"ALARM START");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /*
                                Intent i = new Intent(getApplicationContext(),AlarmActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                */
                                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, 0);
                                try {
                                    pendingIntent.send();
                                } catch (Throwable e){
                                    Log.d(TAG,e.getMessage());
                                }
                            }
                        });

                    }
                }

            }
        });

        // ログ出力（ここに定期実行したい処理を書く）
//       Log.d(TAG, "fuga");

        // 次回の実行について計画を立てる
        makeNextPlan();
    }


    @Override
    public void makeNextPlan()
    {
        this.scheduleNextTime();
    }


    /**
     * もし起動していたら，常駐を解除する
     */
    public static void stopResidentIfActive(Context context) {
        if( activeService != null )
        {
            activeService.stopResident(context);
        }
    }

    public static boolean isServiceRunning() {
        if (activeService != null) {
            return true;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"service destroy");
        super.onDestroy();
    }

    private void setNotification() {
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class), 0);

        Notification notif= new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("テスト通知")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND
                        | Notification.DEFAULT_VIBRATE
                        | Notification.DEFAULT_LIGHTS)
                .setAutoCancel(true)
                .build();


        NotificationManager nm;
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, notif);
    }
    private void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }
}
