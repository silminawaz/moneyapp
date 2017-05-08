package com.ewise.moneyapp.Utils;

import android.content.res.Resources;
import android.util.Log;

import com.ewise.moneyapp.R;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 21/3/17.
 */
public class PdvApiCallbackCounter {

    private int counter;
    public PdvApiCallbackCounter() {
        counter = 0;
    }

    public int increment(){
        counter++;
        return counter;
    }

    public int decrement(){
        if (counter>0){
            counter--;
        }
        return counter;
    }

    public void reset(){
        counter = 0;
    }

    public void setValue(int counter){
        this.counter = counter;
    }

    public int getValue(){
        return counter;
    }

    public String toString(){
        return Integer.toString(counter);
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = Resources.getSystem().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
