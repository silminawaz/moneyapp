package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.ewise.moneyapp.R;

import java.util.UUID;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 13/4/17.
 */
public class SignonProfile {

    //holds multiple app profiles per user (e.g. "Personal", "Business" etc
    public String id;   //same as name without any spaces and special characters
    public String name;
    public String description;
    public String  base64Image;

    public static String validateProfileName(String profileName, Context context){
        String message="";
        if (!profileName.matches("[A-Za-z0-9]+")) {
            message=context.getString(R.string.profile_name_validation_invalid_chars_message);
        }
        else {
            int min = context.getResources().getInteger(R.integer.ewise_profile_min_length);
            int max = context.getResources().getInteger(R.integer.ewise_profile_max_length);
            if ((profileName.trim().length()<min) || (profileName.trim().length()>max)){
                message = String.format(context.getString(R.string.profile_name_validation_invalid_length_message), Integer.toString(min), Integer.toString(max));
            }
        }

        return message;
    }

}
