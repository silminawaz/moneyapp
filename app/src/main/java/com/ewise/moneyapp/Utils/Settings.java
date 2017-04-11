package com.ewise.moneyapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SilmiNawaz on 9/4/17.
 */
public class Settings {

    public static final String SETTINGS_USER_NAME = "com.ewise.moneyapp.username";
    public static final String SETTINGS_USER_EMAIL = "com.ewise.moneyapp.email";

    private EncryptedPin encryptedPin;
    private Context context;

    private static Settings settings = null;


    private Settings(Context context){
        this.context = context;
        encryptedPin = new EncryptedPin(context);
    }

    public static Settings getInstance(Context context){
        if (settings==null){
            settings = new Settings(context);
        }
        return settings;
    }

    public EncryptedPin getEncryptedPin() {
        return encryptedPin;
    }

    public String getUserName(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(SETTINGS_USER_NAME)){
            return sharedPref.getString(SETTINGS_USER_NAME, "");
        }

        return "";

    }

    public boolean setUserName(String userName, Activity activity){
        if (userName!=null) {
            if (userName.length()>0) {
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SETTINGS_USER_NAME, userName);
                editor.apply();
                return true;
            }
        }
        return false;
    }

    public String getUserEmail(Activity activity) {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.contains(SETTINGS_USER_EMAIL)){
            return sharedPref.getString(SETTINGS_USER_EMAIL, "");
        }

        return "";

    }

    public boolean getUserEmail(String userName, Activity activity){
        if (userName!=null) {
            if (userName.length()>0) {
                SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(SETTINGS_USER_EMAIL, userName);
                editor.apply();
                return true;
            }
        }
        return false;
    }
}
