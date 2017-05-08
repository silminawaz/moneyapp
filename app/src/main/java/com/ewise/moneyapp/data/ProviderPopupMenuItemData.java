package com.ewise.moneyapp.data;

import android.content.res.Resources;
import android.util.Log;

import com.ewise.moneyapp.R;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 13/3/17.
 */
public class ProviderPopupMenuItemData {

    public final int icon;
    public final String text;

    public ProviderPopupMenuItemData(int icon, String text){
        this.icon = icon;
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = Resources.getSystem().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
