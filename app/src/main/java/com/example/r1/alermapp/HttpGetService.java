package com.example.r1.alermapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.r1.alermapp.util.NotificationSoundManager;
import com.example.r1.alermapp.util.SampleConst;
import com.example.r1.alermapp.util.Settings;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpGetService extends Service {

    public static HttpGetService acriveService;
    public static int mInterval=30000;

    private int errcnt=0;
    private  static final String TAG = HttpGetService.class.getSimpleName();
    private NotificationSoundManager mNSM=null;

    private int mForceSettingUpdate = 0;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private Runnable mRunTask = new Runnable() {
        @Override
        public void run() {
            execTask();
            mHandler.postDelayed(mRunTask,mInterval);
        }
    };

    public HttpGetService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mHandlerThread = new HandlerThread("other");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        mHandler.post(mRunTask);

        IntentFilter filter = new IntentFilter();
        filter.addAction(SampleConst.WIFISTATEINTENT);

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mWifiReceiver,filter);

        acriveService = this;
        return START_STICKY;
    }

    private void execTask() {

        if (mForceSettingUpdate==1) {
            okhttp3sample();
        }

        if (mNSM==null){
            mNSM = new NotificationSoundManager(getApplicationContext(),true);
        }

        Ion.with(getApplicationContext())
                .load(SampleConst.APIURL + System.currentTimeMillis())
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
                            Log.d(TAG, "fail " + errcnt + " " + emsg);
                            sendBroadcastMessage(emsg);
                            if (errcnt > 0 && errcnt % 10 == 0) {
                                Log.d(TAG, "exit service ");
                                /*
                                delaystart();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                */
                                startMainAndQuit();
                            }

                        } else {
                            errcnt = 0;
                            Log.d(TAG, "result: " + result);
                            sendBroadcastMessage(result);
                            if (result.contains("NG")) {
                                //応答にNGがあった場合なにかする。
                                Log.d(TAG, "NG Detect");
                                setNotification();
                            }
                            if (result.contains("TASK")) {
                                Log.d(TAG, "TASK Detect");
                                startMainAndQuit();

                            }
                        }

                    }
                });
    }

    private void startMainAndQuit() {
        //アクテビティを起動
        Intent userintent = new Intent(getApplicationContext(), MainActivity.class);
        userintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        userintent.putExtra("STARTFLG","STARTQUIT");
        getApplication().startActivity(userintent);
    }
    private void setNotification() {
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0,
                new Intent(this, MainActivity.class), 0);

        if (mNSM == null) {
            mNSM = new NotificationSoundManager(getApplicationContext(),false);
        }
        Uri soundUri = mNSM.getUri(Settings.loadInt(getApplicationContext(),"SOUNDNO"));

        Notification notif= new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("テスト通知")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE
                        | Notification.DEFAULT_LIGHTS)
                .setSound(soundUri)
                .setAutoCancel(true)
                .build();


        NotificationManager nm;
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(1, notif);
    }
    private void sendBroadcastMessage(String msg) {
        Intent i = new Intent();

        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss ");
        final Date date = new Date(System.currentTimeMillis());
        df.format(date);
        i.setAction(SampleConst.LOGMSGINTENT);
        i.putExtra("msg",df.format(date)+msg);


        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(i);
        savePref(df.format(date)+msg);
    }

    private void savePref(String msg) {
        try {
            ArrayList<String> l = Settings.loadList(getApplicationContext(),"APPLOG");
            if (l.size()>30) {
                l.remove(l.size()-1);
            }
            l.add(0,msg);
            Settings.saveList(getApplicationContext(),"APPLOG",l);
            //Log.d(TAG,"listcount "+l.size());
        }catch(Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunTask);
        mHandlerThread.quit();
        acriveService=null;
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mWifiReceiver);
        Log.d(TAG,"ondestroy");
    }
    public static boolean isServiceRunning() {
        if (acriveService==null) return false;
        return true;
    }
    public static void updateInterval(int interval) {
        if (interval<5000) {
            interval = 5000;
        }
        if (interval > 120000) {
            interval=120000;
        }
        if (mInterval!=interval) {
            Log.d(TAG,"updateInterval "+mInterval+" TO "+interval);
            mInterval=interval;
        }
        return;
    }
    public static void updateWifiState(int state) {
        Log.d(TAG,"WifiChanged "+state);
    }

    private void okhttp3sample() {
        final Request request = new Request.Builder()
                // URLを生成
                .url(SampleConst.APIURL)
                .get()
                .build();
        // クライアントオブジェクトを作成する
        final OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            ResponseBody body = response.body();
            String b = body.string();
            Log.d(TAG,"body "+b);
            if (b.indexOf("OK")>=0) {
                if (mForceSettingUpdate ==1) mForceSettingUpdate = 0;
            }
            response.close();
        }catch(IOException ex) {
            ex.printStackTrace();
        }

        // 新しいリクエストを行う

    }
    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra("status",-1);
            //invalid
            if (status == -1) return;

            Log.d(TAG,"WifiState Changed "+status);
            if (status == 1) {
                mForceSettingUpdate = 1;
            }
        }
    };
}

