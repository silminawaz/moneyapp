package com.ewise.moneyapp.Utils;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 5/3/17.
 */

public enum PdvApiStatus {
    PDV_API_STATUS_NOTSTARTED (0),
    PDV_API_STATUS_INPROGRESS (1),
    PDV_API_STATUS_COMPLETED (2);

    private int intValue;

    private PdvApiStatus(int i){
        intValue = i;
    }

    public boolean equalsValue(int i){
        return (intValue == i);
    }

    public int toInt () { return intValue; }

    public int toggleStatus()
    {
        if (this.intValue < 2){
            this.intValue++;
        }

        return this.toInt();
    }
}
