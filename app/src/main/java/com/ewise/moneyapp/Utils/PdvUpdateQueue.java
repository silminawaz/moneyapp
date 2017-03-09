package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.util.Log;
import com.ewise.moneyapp.R;

/**
 * Created by SilmiNawaz on 5/3/17.
 */
public class PdvUpdateQueue {

    Context context;




    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
