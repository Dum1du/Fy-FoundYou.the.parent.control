package com.ghost_byte.fy_foundyoutheparentcontroll;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public PrefManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}

