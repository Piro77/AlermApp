package com.example.r1.alermapp;

import android.content.Context;
import android.content.Intent;

/**
 * 端末起動時の処理。
 * @author id:language_and_engineering
 *
 */
public class OnBootReceiver extends BaseOnBootReceiver
{
    @Override
    protected void onDeviceBoot(Context context)
    {
        // サンプルのサービス常駐を開始
        //new SamplePeriodicService().startResident(context);
        Intent i = new Intent(context.getApplicationContext(), HttpGetService.class);
        context.startService(i);

    }

}