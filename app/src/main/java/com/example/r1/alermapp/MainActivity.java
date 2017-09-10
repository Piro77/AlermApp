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
    private final static String SAVEARRAYITEM = "savearrayitem";
    private static Toast toast;
    private ListView mListview;
    ArrayAdapter<String> mArrayAdapter;

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
            int cnt;
            String oldmsg;
            cnt = mArrayAdapter.getCount();
            if (cnt > 30) {
                oldmsg=mArrayAdapter.getItem(cnt-1);
                mArrayAdapter.remove(oldmsg);
            }
            mArrayAdapter.insert(intent.getStringExtra("msg"),0);
            mArrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(receiver);

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int cnt = mArrayAdapter.getCount();
        StringBuilder sb = new StringBuilder();

        for(int i=0;i<cnt;i++) {
            sb.append(mArrayAdapter.getItem(i)).append(",");
        }
        outState.putString(SAVEARRAYITEM,sb.toString());
        Log.d(TAG,"saved "+cnt+" item");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String s = savedInstanceState.getString(SAVEARRAYITEM);
        String[] itemlist = s.split(",");
        for (String val:itemlist) {
            mArrayAdapter.add(val);
        }
        mArrayAdapter.notifyDataSetChanged();
        Log.d(TAG,"restore item");
    }
}
