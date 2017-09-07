package com.example.r1.alermapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static Toast toast;
    private ListView mListview;
    ArrayAdapter<String> mArrayAdapter;
    private static final String[] foods = {"テスト","リンゴ","バナナ","2","3","4","5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (SamplePeriodicService.isServiceRunning()) {
            findViewById(R.id.button4).setEnabled(false);
        }
        else {
            new SamplePeriodicService().startResident(getApplicationContext());
            findViewById(R.id.button4).setEnabled(false);
        }
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                showToast("btn1");
                Log.d(TAG,"click1");
                new SamplePeriodicService().startResident(getApplicationContext());
                v.setEnabled(false);

            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("btn2");
                Log.d(TAG,"btn2");
                SamplePeriodicService.stopResidentIfActive(getApplicationContext());
            }
        });
        mListview = (ListView)findViewById(R.id.listview);


        mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mListview.setAdapter(mArrayAdapter);
        mArrayAdapter.add("1");
        mArrayAdapter.add("2");

        String m = mArrayAdapter.getItem(0);
        Log.d(TAG,m);
        mArrayAdapter.remove(m);
        mArrayAdapter.notifyDataSetChanged();

        IntentFilter filter = new IntentFilter();
        filter.addAction("action2");

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,filter);

    }


    private void showToast(String msg) {
        if (toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(this,msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("", "receive : " + intent.getStringExtra("msg"));
            mArrayAdapter.add(intent.getStringExtra("msg"));
            mArrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);

        super.onDestroy();
    }
}
