package com.ewise.moneyapp.Utils;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 22/2/17.
 */

public enum PdvConnectivityStatus {
    UNKNOWN ("unknown"),
    SUCCESS ("success"),
    ERROR ("error");

    private final String stringValue;

    private PdvConnectivityStatus(String s) {
        stringValue = s;
    }

    public boolean equalsName(String statusString) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return stringValue.equals(statusString);
    }

    public String toString() {
        return this.stringValue;
    }

}
