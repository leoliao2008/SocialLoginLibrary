package com.tgi.libraryfacebooklogin.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LibraryFbLoginSpUtil {
    private static final String SP_NAME="LibraryFbLoginSp";
    private static final String SP_KEY_FB_ACCESS_TOKEN="SP_KEY_FB_ACCESS_TOKEN";

    public static void saveToken(Context context,String token){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(SP_KEY_FB_ACCESS_TOKEN,token).apply();
    }

    public static String getToken(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_KEY_FB_ACCESS_TOKEN,null);
    }

}
