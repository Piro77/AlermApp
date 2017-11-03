package com.example.r1.alermapp.util;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;

import com.example.r1.alermapp.R;

/**
 * Created by r1 on 2017/11/03.
 */

public class NotificationSoundManager {
    private int[] mSoundList = {R.raw.arpeggio,R.raw.youhavenewmessage,R.raw.youvebeeninformed,R.raw.mizukimail};
    private String[] mSoundNameList ={"サウンド１","サウンド２","サウンド３","音声１"};

    private SoundPool mSoundPool;
    private int[] mSoundid;
    private Context mContext;
    private boolean mPoolEnable = false;

    public NotificationSoundManager(Context ctx ,boolean flg)  {
        mContext = ctx;
        mPoolEnable = false;
    }
    public NotificationSoundManager(Context ctx) {

        mContext = ctx;

        AudioAttributes attr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attr)
                .setMaxStreams(mSoundList.length)
                .build();

        mSoundid = new int[mSoundList.length];

        //https://notificationsounds.com/より取得したoggをraw配下においている
        for(int i=0;i<mSoundList.length;i++) {
            mSoundid[i]= mSoundPool.load(ctx, mSoundList[i],1);
        }
        mPoolEnable = true;

    }
    public int getCount() {
        return mSoundList.length;
    }
    public String[] getNameList() {
        return mSoundNameList;
    }


    public void play(int no) {
        if (mPoolEnable &&   isValidNo(no)) {
            mSoundPool.play(mSoundid[no], 1.0F, 1.0F, 0, 0, 1.0F);
        }

    }

    public Uri getUri(int no) {
        return  Uri.parse("android.resource://" + mContext.getPackageName() + "/" + mSoundList[no]);
    }
    private boolean isValidNo(int no) {
        if (no >= 0 && no <= mSoundid.length) return true;
        return false;
    }
}
