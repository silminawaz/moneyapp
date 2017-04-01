package com.ewise.moneyapp.Utils;

/**
 * Created by SilmiNawaz on 5/3/17.
 */

public enum PdvApiName {

    LOGON ("logon"),
    UPDATE_ACCOUNTS_WITH_NEW_CREDENTIALS ("update_accounts_with_new_credentials"),
    UPDATE_ACCOUNTS ("update_accounts"),
    UPDATE_TRANSACTIONS_WITH_NEW_CREDENTIALS ("update_transactions_with_new_credentials"),
    UPDATE_TRANSACTIONS ("update_transactions"),
    SET_VERIFY ("set_verify"),
    RESTORE_ACCOUNTS ("restore_accounts"),
    RESTORE_TRANSACTIONS ("restore_transactions"),
    DO_TRANSFER ("do_transfer"),
    SET_TRANSFER_PROMPTS ("set_transfer_prompts"),
    GET_LOGIN_URLS ("get_login_urls"),
    GET_USER_PROFILE ("get_user_profile"),
    GET_PROMPTS ("get_prompts"),
    GET_INSTITUTIONS ("get_institutions"),
    GET_CREDENTIAL ("get_credential"),
    SET_CREDENTIAL("set_credential"),
    STOP ("stop");

    private final String stringValue;

    private PdvApiName(String s) {
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
