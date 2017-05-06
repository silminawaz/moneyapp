package com.ewise.moneyapp.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by SilmiNawaz on 3/9/16.
 */
public class PdvAccountResponse {

    private static final String TAG = "LoadingAccounts";

    /**
     * status : data
     * message : null
     * errorType : null
     * instId : null
     * accounts : [refer to PdvAccountResponse.json file]
     * verify : null
     */

    public String status;
    public Object message;
    public Object errorType;
    public Object instId;
    public Object verify;
    /**
     * accountId : s101_1103_1970832291*30DE3D85A55C8E660B890DD217D3CB00CF995CEE9
     * instId : s101_1103_1970832291
     * category : CASH
     * accountNumber : XXXXXXXXXX060
     * accountName : Statement Savings
     * accountHash : 0de3d85a55c8e660b890dd217d3cb00cf995cee9
     * balance : 1174.86
     * currency : SGD
     * availBalance : 1174.86
     * updatedAt : 07/27/2016 12:08:03
     * data : [refer to PdvAccountResponse.json file]
     */

    public List<AccountsObject> accounts;
    private boolean accountsRefreshed;

    public void clearAccounts(){
        if (accounts!=null){
            accounts.clear();
        }
    }

    public DataUpdateType addUpdateAccount (AccountsObject account){
        //if an account exists , update it, if not add it
        if (accounts == null) {
            accounts = new ArrayList<AccountsObject>();
        }
        if (account == null) return DataUpdateType.DATA_UPDATE_TYPE_ERROR;
        for (AccountsObject a : accounts){
            if (a.instId.equals(account.instId) && a.accountHash.equals(account.accountHash)){
                synchronized (this){
                    a.updatedAt = account.updatedAt;
                    a.data = account.data;
                    a.currency = account.currency;
                    a.accountId = account.accountId;
                    a.accountName = account.accountName;
                    a.accountNumber = account.accountNumber;
                    a.availBalance = account.availBalance;
                    a.balance = account.balance;
                    a.category = account.category;
                    accountsRefreshed=true;
                    return DataUpdateType.DATA_UPDATE_TYPE_UPDATED;
                }
            }
        }

        accountsRefreshed=true;
        accounts.add(account);
        return DataUpdateType.DATA_UPDATE_TYPE_ADDED;
    }

    public boolean getAccountsRefreshed(){
        return accountsRefreshed;
    }

    public void resetAccountsRefreshed(){
        synchronized (this) {
            accountsRefreshed = false;
        }
    }


    public int removeAccountsForInstId (String instId){
        int removed = 0;
        if (accounts!=null) {
            for (Iterator<AccountsObject> iterator = accounts.iterator(); iterator.hasNext(); ) {
                AccountsObject object = iterator.next();
                if (object.instId.equals(instId)) {
                    iterator.remove();
                    removed++;
                }
            }
        }
        return removed;
    }

    public static PdvAccountResponse objectFromData(String str) {

        return new Gson().fromJson(str, PdvAccountResponse.class);
    }

    public static PdvAccountResponse objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PdvAccountResponse.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PdvAccountResponse> arrayPdvAccountResponseFromData(String str) {

        Type listType = new TypeToken<ArrayList<PdvAccountResponse>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PdvAccountResponse> arrayPdvAccountResponseFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PdvAccountResponse>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class AccountsObject {
        public String accountId;
        public String instId;
        public String category;
        public String accountNumber;
        public String accountName;
        public String accountHash;
        public String balance;
        public String currency;
        public String availBalance;
        public String updatedAt;
        public String data;

        public static AccountsObject clone(AccountsObject acct){
            AccountsObject a=new AccountsObject();
            a.accountId=acct.accountId;
            a.accountName=acct.accountName;
            a.currency=acct.currency;
            a.accountNumber=acct.accountNumber;
            a.balance=acct.balance;
            a.accountHash=acct.accountHash;
            a.availBalance=acct.availBalance;
            a.category=acct.category;
            a.data=acct.data;
            a.instId=acct.instId;
            return a;
        }

        public static AccountsObject objectFromData(String str) {

            return new Gson().fromJson(str, AccountsObject.class);
        }

        public static AccountsObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), AccountsObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<AccountsObject> arrayAccountsObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<AccountsObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<AccountsObject> arrayAccountsObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<AccountsObject>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        @Override
        public String toString() {
            try {
                String jsonString = new Gson().toJson(this);
                return jsonString.toString();
            }
            catch (Exception e){
                Log.e(TAG, e.getMessage());
            }
            return super.toString();
        }
    }
}
