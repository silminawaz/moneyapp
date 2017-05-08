package com.ewise.moneyapp.Utils;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 12/4/17.
 */

public enum SignOnSystem {

    SIGN_ON_SYSTEM_UNKNOWN ("unknown"),
    SIGN_ON_SYSTEM_EWISE ("ewise"),
    SIGN_ON_SYSTEM_GOOGLE ("google"),
    SIGN_ON_SYSTEM_FACEBOOK ("facebook"),
    SIGN_ON_SYSTEM_TWITTER ("twitter"),
    SIGN_ON_SYSTEM_INSTAGRAM ("instagram"),
    SIGN_ON_SYSTEM_LINKEDIN ("linkedin");

    private final String stringValue;

    private SignOnSystem(String s) {
        stringValue = s.toLowerCase();
    }

    public boolean equalsName(String statusString) {
        // (otherName == null) check is not needed because name.equals(null) returns false
        return stringValue.equals(statusString.toLowerCase());
    }

    public String toString() {
        return this.stringValue;
    }

}
