package com.example.modulestepcounter.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.modulestepcounter.constant.ROMConstant;

public class XiaomiSpUtil {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String KEY_WORK_MODE = "currentWorkMode";
    public static final String KEY_TRY_CONVERT_SYSTEM_APP="tryConvertSystemApp";
    public static final String KEY_UNZIP_SQLITE_FILE="isUnzipSqliteFile";
    public static final String KEY_AUTO_ADD_STEPS="AutoAddSteps";
    public static final String KEY_NEW_DAY_AUTO_ADD="isNewDayAutoAddSteps";
    public static final String KEY_CURRENT_DAY="currentDayInt";

    public XiaomiSpUtil(Context context) {
        preferences = context.getSharedPreferences("app_setting", Context.MODE_PRIVATE);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, ROMConstant.BOOL_NULL);
    }

    public int getInt(String key) {
        return preferences.getInt(key, ROMConstant.INTEGER_NULL);
    }

    public XiaomiSpUtil getEdit() {
        if (editor == null) {
            editor = preferences.edit();
        }
        return this;
    }

    public XiaomiSpUtil putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public XiaomiSpUtil putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public void editApply() {
        editor.apply();
    }
}
