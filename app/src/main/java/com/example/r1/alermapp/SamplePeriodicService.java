package com.example.r1.alermapp;
import android.app.AlarmManager;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;


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

    private static int errcnt = 0;


    @Override
    protected long getIntervalMS() {
        //10時から9時までは5分おきその他は一時間
        Calendar calendar = Calendar.getInstance();
        int hh = calendar.get(Calendar.HOUR_OF_DAY);
        int mm = calendar.get(Calendar.MINUTE);
        Log.d(TAG,"Hour "+String.valueOf(hh)+" Min "+String.valueOf(mm));
        if ((hh > 21) || (hh < 9)) {
            return 1000 * 120;
        }
        return 1000 * 120;
    }


    @Override
    protected void execTask() {
        activeService = this;

        Ion.with(getApplicationContext())
                .load("http://192.168.1.23/api/chk/check.html?"+System.currentTimeMillis())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            errcnt++;
                            String emsg = e.getMessage();
                            if (emsg == null) {
                                emsg = e.toString();
                            }
                            Log.d(TAG,"fail "+errcnt+" "+emsg);
                            sendBroadcastMessage(emsg);
                            if (errcnt > 0 && errcnt % 10 == 0) {
                                Log.d(TAG,"exit service ");

                                delaystart();
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }

                        }
                        else {
                            errcnt=0;
                            Log.d(TAG, "result: " + result);
                            if (result.contains("NG")) {
                                //応答にNGがあった場合なにかする。
                                Log.d(TAG,"NG Detect");
                                setNotification();
                            }
                        }

                        makeNextPlan();
                    }
                });



        // ※もし毎回の処理が重い場合は，メインスレッドを妨害しないために
        // ここから下を別スレッドで実行する。
        /*

        OkHttpClient client = OkHttpSingleton.getInstance().getOkHttpClient();

        final Request request = new Request.Builder()
                .url("http://dtivps.srzp.net/api/chk/check.html?"+System.currentTimeMillis())
                .cacheControl(new CacheControl.Builder().noCache().build())
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失敗しても特に何もしない。
                errcnt++;
                Log.d(TAG,"fail "+errcnt+" "+e.getMessage());
                makeNextPlan();

                if (errcnt > 0 && errcnt % 10 == 0) {
                    Log.d(TAG,"exit service ");

                    delaystart();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                makeNextPlan();
                errcnt=0;
                if (response.isSuccessful()) {
                    Log.d(TAG,"Success");
                    String body = response.body().string();
                    response.close();
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
        */
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

    private void delaystart() {
        long ct = System.currentTimeMillis(); //get current time
        Intent restartService = new Intent(getApplicationContext(),
                OnBootReceiver.class);
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 0, restartService,
                0);

        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        mgr.setRepeating(AlarmManager.RTC_WAKEUP, ct, 1 * 1000, restartServicePI);
    }
    private void sendBroadcastMessage(String msg) {
        Intent i = new Intent();
        i.setAction("action2");
        i.putExtra("msg",msg);


        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
    }
}
