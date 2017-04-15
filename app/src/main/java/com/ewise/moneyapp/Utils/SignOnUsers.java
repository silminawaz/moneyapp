package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 12/4/17.
 * Singleton implemntation of SignOnUsers
 */
public class SignOnUsers {

    private static final String TAG = "SignOnUsers";


    //private static SignOnUsers signOnUsers = null;

    //private Context context;

    //stores all signon users in the system and their profile info
    List<SignonUser> users;

    public SignOnUsers (){

        users = new ArrayList<>();

    }


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString, Exception e, Context context) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
        e.printStackTrace();
    }

}
