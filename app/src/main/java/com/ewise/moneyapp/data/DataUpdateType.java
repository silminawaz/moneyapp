package com.ewise.moneyapp.data;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 21/3/17.
 */

public enum DataUpdateType {
    DATA_UPDATE_TYPE_ERROR (0),
    DATA_UPDATE_TYPE_UPDATED (1),
    DATA_UPDATE_TYPE_ADDED (2);

    private int intValue;

    private DataUpdateType(int i){
        intValue = i;
    }

    public boolean equalsValue(int i){
        return (intValue == i);
    }

    public int toInt () { return intValue; }

}
