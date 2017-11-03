package com.example.r1.alermapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.r1.alermapp.util.NotificationSoundManager;
import com.example.r1.alermapp.util.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.RunnableFuture;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String SAVEARRAYITEM = "savearrayitem";
    private static Toast toast;
    private ListView mListview;
    private Spinner mSpinner;
    ArrayAdapter<String> mArrayAdapter;
    ArrayAdapter<String> mArrayAdapterSpinner;
    private NotificationSoundManager mNotificationSoundManager;
    private Boolean mSpinnerReadyFlg = false;

    //画面終了用関数
    private final Runnable finishfunc = new Runnable() {
        @Override
        public void run() {
            moveTaskToBack(true);
            Log.d(TAG,"finishfunc called");
        }
    };

    private final Runnable spinnersoundenabler = new Runnable() {
        @Override
        public void run() {
            mSpinnerReadyFlg=true;
            Log.d(TAG,"spinner select sound enabled");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String value = intent.getStringExtra("STARTFLG");//設定したkeyで取り出す
        if (value != null && value.equals("STARTQUIT")) {
            Log.d(TAG,"force finish");
            new Handler().postDelayed(finishfunc,1000);
        }


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

        mListview = (ListView) findViewById(R.id.listview);

        mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mListview.setAdapter(mArrayAdapter);

        ArrayList<String> l = Settings.loadList(getApplicationContext(),"APPLOG");
        mArrayAdapter.addAll(l);

        mNotificationSoundManager = new NotificationSoundManager(getApplicationContext());

        mSpinner = (Spinner)findViewById(R.id.spinner);

        mArrayAdapterSpinner = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,mNotificationSoundManager.getNameList());
        mArrayAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mArrayAdapterSpinner);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSpinnerReadyFlg) {
                    mNotificationSoundManager.play(i);
                    Settings.saveInt(getApplicationContext(),"SOUNDNO",i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("action2");

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver,filter);


        mSpinner.setSelection(Settings.loadInt(getApplicationContext(),"SOUNDNO"),false);

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

            mNotificationSoundManager = new NotificationSoundManager(getApplicationContext());
            //spinner選択後に音が鳴る処理を5秒後に有効にする
            new Handler().postDelayed(spinnersoundenabler,5000);
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
