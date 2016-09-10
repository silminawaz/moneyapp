package com.ewise.moneyapp.data;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
