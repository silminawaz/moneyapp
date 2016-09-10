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
public class PdvTransactionResponse {

    /**
     * status : success
     * message : null
     * errorType : null
     * instId : null
     * verify : null
     * accountTransactions : [refer to PdvCashTransactionResponse.json file]
     * accountsUpdated : []
     * responses : null
     */

    public String status;
    public Object message;
    public Object errorType;
    public Object instId;
    public Object verify;
    public Object responses;
    /**
     * instId : 1103
     * accountId : 0de3d85a55c8e660b890dd217d3cb00cf995cee9
     * transactions : [{"id":213,"date":"2016-06-06","description":"\"SVC CHG SERVICE CHARGE 01MAY16 TO 31MAY16 ZDD407447 SYSTEM GENERATED\"","amount":-5,"currency":"SGD","data":"undefined","fingerprint":"GZ-sxVDQoSvoLLW_xDZA2WqX6DOZnSNKUo7EoHQ6FLU"},{"id":212,"date":"2016-06-15","description":"\"DEBIT AS ADVISED TO DINERS CREDIT CARD **************** diners due 27JUN16 HIB- 293756X303605 IB0233877 INTERNET BANKING\"","amount":-467.16,"currency":"SGD","data":"undefined","fingerprint":"_KUUfE1z8nfXhh-5egmRPzpNVG7EQTmIC9FvLR9wKpY"},{"id":211,"date":"2016-06-24","description":"\"INTEREST CREDIT INTEREST ZDD400011 SYSTEM GENERATED\"","amount":0.01,"currency":"SGD","data":"undefined","fingerprint":"-3alYvB6ygdDwaLNavzBojq2-ii9GlqFd51ioKDOnK8"}]
     * account : refer to json files in assets directory
     */

    public List<AccountTransactionsObject> accountTransactions;
    public List<?> accountsUpdated;

    public static PdvTransactionResponse objectFromData(String str) {

        return new Gson().fromJson(str, PdvTransactionResponse.class);
    }

    public static PdvTransactionResponse objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PdvTransactionResponse.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PdvTransactionResponse> arrayPdvTransactionResponseFromData(String str) {

        Type listType = new TypeToken<ArrayList<PdvTransactionResponse>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PdvTransactionResponse> arrayPdvTransactionResponseFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PdvTransactionResponse>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    /**
     * class: PdvTransactionResponse
     */
    @Override
    public String toString() {
        try {
            String jsonString = new Gson().toJson(this);
            return jsonString.toString();
        }
        catch (Exception e){
            Log.e("EXCEPTION", e.getMessage());
        }
        return super.toString();
    }

    public static class AccountTransactionsObject {
        public String instId;
        public String accountId;
        /**
         * accountId : s101_1103_1970832291*30DE3D85A55C8E660B890DD217D3CB00CF995CEE9
         * instId : s101_1103_1970832291
         * category : CASH
         * accountNumber : ************060
         * accountName : Statement Savings
         * accountHash : 0de3d85a55c8e660b890dd217d3cb00cf995cee9
         * balance : 1174.86
         * currency : SGD
         * availBalance : 1174.86
         * updatedAt : 07/27/2016 12:08:03
         * data : refer to json files in assets directory         */

        public AccountObject account;
        /**
         * id : 213
         * date : 2016-06-06
         * description : "SVC CHG SERVICE CHARGE 01MAY16 TO 31MAY16 ZDD407447 SYSTEM GENERATED"
         * amount : -5
         * currency : SGD
         * data : undefined
         * fingerprint : GZ-sxVDQoSvoLLW_xDZA2WqX6DOZnSNKUo7EoHQ6FLU
         */

        public List<TransactionsObject> transactions;

        public static AccountTransactionsObject objectFromData(String str) {

            return new Gson().fromJson(str, AccountTransactionsObject.class);
        }

        public static AccountTransactionsObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), AccountTransactionsObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<AccountTransactionsObject> arrayAccountTransactionsObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<AccountTransactionsObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<AccountTransactionsObject> arrayAccountTransactionsObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<AccountTransactionsObject>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }

        /**
         * class: PdvTransactionResponse.AccountTransactionObject
         */
        @Override
        public String toString() {
            try {
                String jsonString = new Gson().toJson(this);
                return jsonString.toString();
            }
            catch (Exception e){
                Log.e("EXCEPTION", e.getMessage());
            }
            return super.toString();
        }

        /**
         * class: PdvTransactionResponse.AccountTransactionObject.AccountObject
         */
        public static class AccountObject {
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

            public static AccountObject objectFromData(String str) {

                return new Gson().fromJson(str, AccountObject.class);
            }

            public static AccountObject objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), AccountObject.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<AccountObject> arrayAccountObjectFromData(String str) {

                Type listType = new TypeToken<ArrayList<AccountObject>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<AccountObject> arrayAccountObjectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<AccountObject>>() {
                    }.getType();

                    return new Gson().fromJson(jsonObject.getString(str), listType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return new ArrayList();


            }

            public PdvAccountResponse.AccountsObject getAccountsObject(){
                PdvAccountResponse.AccountsObject accountsObject = new PdvAccountResponse.AccountsObject();
                accountsObject.accountId = this.accountId;
                accountsObject.instId = this.instId;
                accountsObject.category = this.category;
                accountsObject.accountNumber = this.accountNumber;
                accountsObject.accountName = this.accountName;
                accountsObject.accountHash = this.accountHash;
                accountsObject.balance = this.balance;
                accountsObject.currency = this.currency;
                accountsObject.availBalance = this.availBalance;
                accountsObject.updatedAt = this.updatedAt;
                accountsObject.data = this.data;
                return accountsObject;
            }

            @Override
            public String toString() {
                try {
                    String jsonString = new Gson().toJson(this);
                    return jsonString.toString();
                }
                catch (Exception e){
                    Log.e("EXCEPTION", e.getMessage());
                }
                return super.toString();
            }
        }

        /**
         * class: PdvTransactionResponse.AccountTransactionObject.TransactionsObject
         */
        public static class TransactionsObject {
            public int id;
            public String date;
            public String description;
            public double amount;
            public String currency;
            public String data;
            public String fingerprint;

            public static TransactionsObject objectFromData(String str) {

                return new Gson().fromJson(str, TransactionsObject.class);
            }

            public static TransactionsObject objectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);

                    return new Gson().fromJson(jsonObject.getString(str), TransactionsObject.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            public static List<TransactionsObject> arrayTransactionsObjectFromData(String str) {

                Type listType = new TypeToken<ArrayList<TransactionsObject>>() {
                }.getType();

                return new Gson().fromJson(str, listType);
            }

            public static List<TransactionsObject> arrayTransactionsObjectFromData(String str, String key) {

                try {
                    JSONObject jsonObject = new JSONObject(str);
                    Type listType = new TypeToken<ArrayList<TransactionsObject>>() {
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
                    Log.e("EXCEPTION", e.getMessage());
                }
                return super.toString();
            }
        }
    }
}
