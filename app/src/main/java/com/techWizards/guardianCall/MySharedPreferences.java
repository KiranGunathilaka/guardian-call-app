package com.techWizards.guardianCall;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String STRING_KEY = "myString";

    public static void saveString(Context context, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(STRING_KEY, value);
        editor.apply();
    }


    public static String getString(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(STRING_KEY, "");
    }

}
