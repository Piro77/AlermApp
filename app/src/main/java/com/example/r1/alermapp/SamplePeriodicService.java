package com.example.r1.alermapp;
import android.content.Context;
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
        if ((hh > 21) || (hh < 10)) {
            return 1000 * 3600;
        }
        return 1000 * 300;
    }


    @Override
    protected void execTask() {
        activeService = this;


        // ※もし毎回の処理が重い場合は，メインスレッドを妨害しないために
        // ここから下を別スレッドで実行する。
        OkHttpClient client = OkHttpSingleton.getInstance().getOkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.231/index.html")
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG,"fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG,"Success");
            }
        });

        // ログ出力（ここに定期実行したい処理を書く）
       Log.d(TAG, "fuga");

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

}
