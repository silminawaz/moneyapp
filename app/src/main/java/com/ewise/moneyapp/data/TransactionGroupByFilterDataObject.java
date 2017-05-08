package com.ewise.moneyapp.data;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 16/9/16.
 */
public class TransactionGroupByFilterDataObject {

    public int filterIcon;
    public String filterIconText;
    public String filterLabel;

    public TransactionGroupByFilterDataObject (int filterIcon, String filterLabel){

        this.filterIcon = filterIcon;
        this.filterLabel = filterLabel;
        this.filterIconText = filterLabel.substring(0,1).toUpperCase();
    }
}
