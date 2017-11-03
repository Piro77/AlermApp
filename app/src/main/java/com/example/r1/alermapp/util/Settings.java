package com.example.r1.alermapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by r1 on 2017/10/11.
 */

public class Settings {
    // 設定値 ArrayList<String> を保存（Context は Activity や Application や Service）
    public static void saveList(Context ctx, String key, ArrayList<String> list) {
        JSONArray jsonAry = new JSONArray();
        for(int i=0; i<list.size(); i++) {
            jsonAry.put(list.get(i));
        }
        SharedPreferences prefs = ctx.getSharedPreferences("APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, jsonAry.toString());
        editor.apply();
    }

    // 設定値 ArrayList<String> を取得（Context は Activity や Application や Service）
    public static ArrayList<String> loadList(Context ctx, String key) {
        ArrayList<String> list = new ArrayList<String>();
        SharedPreferences prefs = ctx.getSharedPreferences("APP", Context.MODE_PRIVATE);
        String strJson = prefs.getString(key, ""); // 第２引数はkeyが存在しない時に返す初期値
        if(!strJson.equals("")) {
            try {
                JSONArray jsonAry = new JSONArray(strJson);
                for(int i=0; i<jsonAry.length(); i++) {
                    list.add(jsonAry.getString(i));
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }
    // 設定値 int を保存（Context は Activity や Application や Service）
    public static void saveInt(Context ctx, String key, int val) {
        SharedPreferences prefs = ctx.getSharedPreferences("APP", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, val);
        editor.apply();
    }

    // 設定値 int を取得（Context は Activity や Application や Service）
    public static int loadInt(Context ctx, String key) {
        SharedPreferences prefs = ctx.getSharedPreferences("APP", Context.MODE_PRIVATE);
        return prefs.getInt(key, 0); // 第２引数はkeyが存在しない時に返す初期値
    }
}
