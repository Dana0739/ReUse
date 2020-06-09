package com.example.playground1.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferencesUtils {

    private static String CURRENT_ID = "current_id";
    private static String EMPTY = "";

    public static void saveId(Activity activity, String value) {
        saveText(activity, CURRENT_ID, value);
    }

    public static String loadId(Activity activity) {
        return loadText(activity, CURRENT_ID);
    }

    public static void saveText(Activity activity, String vName, String value) {
        SharedPreferences sPref = activity.getSharedPreferences("ContactData", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(vName, value);
        ed.apply();
        ed.commit();
    }

    public static String loadText(Activity activity, String varName) {
        SharedPreferences sPref = activity.getSharedPreferences("ContactData", MODE_PRIVATE);
        return sPref.getString(varName, EMPTY);
    }

    public static void initializeEmpty(Activity activity) {
        saveText(activity, "current_id", EMPTY);
        saveText(activity, "current_name", EMPTY);
    }
}
