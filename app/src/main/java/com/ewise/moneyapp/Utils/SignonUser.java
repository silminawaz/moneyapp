package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 13/4/17.
 */
public class SignonUser {

    private static final String TAG = "SignonUser";

    //holds a single signon users profile
    public SignOnSystem system;
    public String id; //unique id from signon system
    public String name;
    public String firstName;
    public String middleName;
    public String lastName;
    public String email;
    public String imageURLPath;
    public String base64Image;
    public List<SignonProfile> profiles;
    private String encryptedPIN;


    public SignonUser(){
        profiles = new ArrayList<>();
    }

    public String getEncryptedPIN() {
        return encryptedPIN;
    }

    public void setEncryptedPIN(String encryptedPIN) {
        this.encryptedPIN = encryptedPIN;
    }



    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString, Exception e, Context context) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
        e.printStackTrace();
    }
}
