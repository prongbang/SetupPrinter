package com.prongbang.setupprinter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by prongbang on 6/3/2016.
 */
public class SessionManager {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "xE4Pref";

    public static final String MODEL_PRINTER = "modelPrinter";

    public static final String KEY_PRINTER = "keyPrinter";

    public static final String CONNECTED_PRINTER = "connectedPrinter";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setModelPrinter(String modelId) {
        editor.putString(MODEL_PRINTER, modelId);
        editor.commit();
    }

    public String getModelPrinter() {
        return pref.getString(MODEL_PRINTER, null);
    }

    public void setKeyPrinter(String deviceId) {
        editor.putString(KEY_PRINTER, deviceId);
        editor.commit();
    }

    public String getKeyPrinter() {
        return pref.getString(KEY_PRINTER, null);
    }

    public void setConnectedPrinter(String modelPrinter) {
        editor.putString(CONNECTED_PRINTER, modelPrinter);
        editor.commit();
    }

    public String getConnectedPrinter() {
        return pref.getString(CONNECTED_PRINTER, null);
    }
}
