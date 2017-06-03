package com.example.r1.alermapp;

import android.content.Context;

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
        new SamplePeriodicService().startResident(context);
    }

}