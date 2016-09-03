package com.ewise.moneyapp.data;

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
public class PdvAccounts {

    /**
     * status : data
     * message : null
     * errorType : null
     * instId : null
     * accounts : [{"accountId":"s101_1103_1970832291*30DE3D85A55C8E660B890DD217D3CB00CF995CEE9","instId":"s101_1103_1970832291","category":"CASH","accountNumber":"XXXXXXXXXX060","accountName":"Statement Savings","accountHash":"0de3d85a55c8e660b890dd217d3cb00cf995cee9","balance":"1174.86","currency":"SGD","availBalance":"1174.86","updatedAt":"07/27/2016 12:08:03","data":"{\"Portfolio\":{\"PortfolioNumber\":\"\",\"PortfolioName\":\"MR MOHAMED SILMI NAWAZ\",\"PortfolioValuationCurrency\":\"\",\"PortfolioTotalCurrentValue\":\"\",\"PortfolioLastUpdatedDateTime\":\"20160727\"},\"PortfolioHoldingId\":\"************060\",\"PortfolioHoldingName\":\"Statement Savings\",\"PortfolioHoldingAcctCategory\":\"Cash\",\"PortfolioHoldingAcctSubCategory\":\"Cash_Savings\",\"PortfolioHoldingValuationCurrency\":\"SGD\",\"PortfolioHoldingLastUpdatedDateTime\":\"20160727\",\"PortfolioHoldingTotalCurrentValue\":\"1174.86\",\"PortfolioHoldingsAssetList\":{}}"},{"accountId":"s101_1103_1970832291*5322A1B03E39E95E170A1352DF672ECBDAB2AF701","instId":"s101_1103_1970832291","category":"CREDIT","accountNumber":"************3119","accountName":"VISA/MASTERCARD","accountHash":"322a1b03e39e95e170a1352df672ecbdab2af701","balance":"2803.34","currency":"SGD","availBalance":"3927.77","updatedAt":"07/27/2016 12:08:03","data":"{\"Portfolio\":{\"PortfolioNumber\":\"\",\"PortfolioName\":\"MR MOHAMED SILMI NAWAZ\",\"PortfolioValuationCurrency\":\"\",\"PortfolioTotalCurrentValue\":\"\",\"PortfolioLastUpdatedDateTime\":\"20160727\"},\"PortfolioHoldingId\":\"************3119\",\"PortfolioHoldingName\":\"VISA/MASTERCARD\",\"PortfolioHoldingAcctCategory\":\"Loan\",\"PortfolioHoldingAcctSubCategory\":\"Cards_CC\",\"PortfolioHoldingValuationCurrency\":\"SGD\",\"PortfolioHoldingLastUpdatedDateTime\":\"20160727\",\"PortfolioHoldingTotalCurrentValue\":\"2803.34\",\"PortfolioHoldingsAssetList\":{}}"},{"accountId":"s101_1103_1970832291*664DE1497BF8156D08B4D81AF03F54CAC9E69409C","instId":"s101_1103_1970832291","category":"MORTGAGE","accountNumber":"************440","accountName":"Mortgage","accountHash":"64de1497bf8156d08b4d81af03f54cac9e69409c","balance":"1132361.35","currency":"SGD","availBalance":"","updatedAt":"07/27/2016 12:08:03","data":"{\"Portfolio\":{\"PortfolioNumber\":\"\",\"PortfolioName\":\"MR MOHAMED SILMI NAWAZ\",\"PortfolioValuationCurrency\":\"\",\"PortfolioTotalCurrentValue\":\"\",\"PortfolioLastUpdatedDateTime\":\"20160727\"},\"PortfolioHoldingId\":\"************440\",\"PortfolioHoldingName\":\"Mortgage\",\"PortfolioHoldingAcctCategory\":\"Loan\",\"PortfolioHoldingAcctSubCategory\":\"Loan_Mortgage\",\"PortfolioHoldingValuationCurrency\":\"SGD\",\"PortfolioHoldingLastUpdatedDateTime\":\"20160727\",\"PortfolioHoldingTotalCurrentValue\":\"1132361.35\",\"PortfolioHoldingsAssetList\":{}}"},{"accountId":"s101_1103_1970832291*276049A7FBF86D1051CF29F0C8D098011C31DE162","instId":"s101_1103_1970832291","category":"CASH","accountNumber":"************490","accountName":"Personal Line of Credit","accountHash":"76049a7fbf86d1051cf29f0c8d098011c31de162","balance":"1320.14 Dr","currency":"SGD","availBalance":"3679.86","updatedAt":"07/27/2016 12:08:03","data":"{\"Portfolio\":{\"PortfolioNumber\":\"\",\"PortfolioName\":\"MR MOHAMED SILMI NAWAZ\",\"PortfolioValuationCurrency\":\"\",\"PortfolioTotalCurrentValue\":\"\",\"PortfolioLastUpdatedDateTime\":\"20160727\"},\"PortfolioHoldingId\":\"************490\",\"PortfolioHoldingName\":\"Personal Line of Credit\",\"PortfolioHoldingAcctCategory\":\"Cash\",\"PortfolioHoldingAcctSubCategory\":\"Cash_Savings\",\"PortfolioHoldingValuationCurrency\":\"SGD\",\"PortfolioHoldingLastUpdatedDateTime\":\"20160727\",\"PortfolioHoldingTotalCurrentValue\":\"1320.14\",\"PortfolioHoldingsAssetList\":{}}"},{"accountId":"s101_1103_1970832291*495994F9F32D30F70F8A4D5CB10AAF32927F697A8","instId":"s101_1103_1970832291","category":"CASH","accountNumber":"************496","accountName":"Premier Account","accountHash":"95994f9f32d30f70f8a4d5cb10aaf32927f697a8","balance":"20.86","currency":"SGD","availBalance":"20.86","updatedAt":"07/27/2016 12:08:03","data":"{\"Portfolio\":{\"PortfolioNumber\":\"\",\"PortfolioName\":\"MR MOHAMED SILMI NAWAZ\",\"PortfolioValuationCurrency\":\"\",\"PortfolioTotalCurrentValue\":\"\",\"PortfolioLastUpdatedDateTime\":\"20160727\"},\"PortfolioHoldingId\":\"************496\",\"PortfolioHoldingName\":\"Premier Account\",\"PortfolioHoldingAcctCategory\":\"Cash\",\"PortfolioHoldingAcctSubCategory\":\"Cash_Savings\",\"PortfolioHoldingValuationCurrency\":\"SGD\",\"PortfolioHoldingLastUpdatedDateTime\":\"20160727\",\"PortfolioHoldingTotalCurrentValue\":\"20.86\",\"PortfolioHoldingsAssetList\":{}}"},{"accountId":"s101_1201_4601080165*294EAF3A059429A089D51325A3F0CEE2F7AC6A6269","instId":"s101_1201_4601080165","category":"CASH","accountNumber":"1111 1111 11","accountName":"Dummy Account 01Longest Name","accountHash":"4eaf3a059429a089d51325a3f0cee2f7ac6a6269","balance":"999.000000","currency":"USD","availBalance":"1333.150000","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*3059183736400CE8360E99327138C40770D44120F5","instId":"s101_1201_4601080165","category":"CASH","accountNumber":"2222222222","accountName":"Pau's Cash Account","accountHash":"59183736400ce8360e99327138c40770d44120f5","balance":"1010.010000","currency":"SGD","availBalance":"2222.000000","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*335CC2EE439537F42A270BCC1E7EA54D2F81B3DA55","instId":"s101_1201_4601080165","category":"INSURANCE","accountNumber":"00998877665544332211","accountName":"Chrome User01 - Insurance","accountHash":"5cc2ee439537f42a270bcc1e7ea54d2f81b3da55","balance":"1333.000000","currency":"GBP","availBalance":"","updatedAt":"08/18/2016 09:55:27","data":"undefined"},{"accountId":"s101_1201_4601080165*4197AB156BAD2B6B4B980718C32EE0846F655810DB","instId":"s101_1201_4601080165","category":"SUPERANNUATION","accountNumber":"3333 3333 33","accountName":"Dummy Account 03","accountHash":"97ab156bad2b6b4b980718c32ee0846f655810db","balance":"999.290000","currency":"GBP","availBalance":"","updatedAt":"08/18/2016 09:55:28","data":"undefined"},{"accountId":"s101_1201_4601080165*359D5B0CFD0D14449CBCF9042EE300991E7F85153B","instId":"s101_1201_4601080165","category":"INVESTMENT","accountNumber":"2222 2222 22","accountName":"Dummy Account 02","accountHash":"9d5b0cfd0d14449cbcf9042ee300991e7f85153b","balance":"1000.200000","currency":"AUD","availBalance":"2000.200000","updatedAt":"08/18/2016 09:55:27","data":"undefined"},{"accountId":"s101_1201_4601080165*32A75E08485770D7B92BC179D86702EF01E0712AE7","instId":"s101_1201_4601080165","category":"INSURANCE","accountNumber":"4444 4444 44","accountName":"Insurance (user01)","accountHash":"a75e08485770d7b92bc179d86702ef01e0712ae7","balance":"1213.410000","currency":"PHP","availBalance":"","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*31AEECF27BC872A29C7A7538F4615E4E865BD1FC68","instId":"s101_1201_4601080165","category":"CREDIT","accountNumber":"6666 6666 66","accountName":"Dummy Account 06","accountHash":"aeecf27bc872a29c7a7538f4615e4e865bd1fc68","balance":"-1226.600000","currency":"JOD","availBalance":"2000.600000","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*28B1776F487BE9EAE7E6579DFF38F1C95EF4689CB5","instId":"s101_1201_4601080165","category":"ASSET","accountNumber":"a1234567","accountName":"ASSET_TEST","accountHash":"b1776f487be9eae7e6579dff38f1c95ef4689cb5","balance":"5592.990000","currency":"GBP","availBalance":"","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*40BC6C7E277D9B95ECF6F0CB698F2FF826A4FC22D9","instId":"s101_1201_4601080165","category":"OVERDRAFT","accountNumber":"1010 1010 10","accountName":"Dummy Account 10","accountHash":"bc6c7e277d9b95ecf6f0cb698f2ff826a4fc22d9","balance":"1222.100000","currency":"INR","availBalance":"","updatedAt":"08/18/2016 09:55:28","data":"undefined"},{"accountId":"s101_1201_4601080165*34D2044EBB97032D729F34B3046C4AE03AD418D7CE","instId":"s101_1201_4601080165","category":"INVESTLOAN","accountNumber":"8888 8888 88","accountName":"Dummy Account 08","accountHash":"d2044ebb97032d729f34b3046c4ae03ad418d7ce","balance":"1001.800000","currency":"CHF","availBalance":"","updatedAt":"08/18/2016 09:55:27","data":"undefined"},{"accountId":"s101_1201_4601080165*39D262610FDD973A56957B3F685983A89E473CD6BB","instId":"s101_1201_4601080165","category":"MORTGAGE","accountNumber":"7777 7777 77","accountName":"Dummy Account 07","accountHash":"d262610fdd973a56957b3f685983a89e473cd6bb","balance":"1000.710000","currency":"AUD","availBalance":"2000.700000","updatedAt":"08/18/2016 09:55:28","data":"undefined"},{"accountId":"s101_1201_4601080165*36D84B1D3AC4437DD1F4AE5772360AFB13B11EE0E7","instId":"s101_1201_4601080165","category":"LIABILITY","accountNumber":"1111 1111 13","accountName":"Liability One","accountHash":"d84b1d3ac4437dd1f4ae5772360afb13b11ee0e7","balance":"1101.100000","currency":"KRW","availBalance":"","updatedAt":"08/18/2016 09:55:27","data":"{\"accountMembers\":[{\"balance\":\"1101.100000\",\"currency\":\"KRW\",\"funds\":\"\",\"name\":\"Liability One\",\"number\":\"1111 1111 13\",\"type\":\"LIABILITY\"},{\"balance\":\"2000.000000\",\"currency\":\"KRW\",\"funds\":\"\",\"name\":\"Liability Two\",\"number\":\"1111 1111 13\",\"type\":\"LIABILITY\"}]}"},{"accountId":"s101_1201_4601080165*38DDF1C4479233B4CF2C76FF0C2108A56A6D6F9ED6","instId":"s101_1201_4601080165","category":"LOAN","accountNumber":"9999 9999 99","accountName":"Dummy Account 09","accountHash":"ddf1c4479233b4cf2c76ff0c2108a56a6d6f9ed6","balance":"1212.890000","currency":"BRL","availBalance":"1212.900000","updatedAt":"08/18/2016 09:55:27","data":"undefined"},{"accountId":"s101_1201_4601080165*27E074CCE723C993148093A3A5148B78631D3210A2","instId":"s101_1201_4601080165","category":"ASSET","accountNumber":"5555 5555 55","accountName":"Dummy Account 05","accountHash":"e074cce723c993148093a3a5148b78631d3210a2","balance":"519.500000","currency":"JPY","availBalance":"","updatedAt":"08/18/2016 09:55:26","data":"undefined"},{"accountId":"s101_1201_4601080165*37F2EB5229DCE65FFE3111F4B191115C90643287DF","instId":"s101_1201_4601080165","category":"LIABILITY","accountNumber":"2211 1111 13","accountName":"My Liability","accountHash":"f2eb5229dce65ffe3111f4b191115c90643287df","balance":"3000.000000","currency":"KRW","availBalance":"","updatedAt":"08/18/2016 09:55:27","data":"undefined"}]
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
     * data : {"Portfolio":{"PortfolioNumber":"","PortfolioName":"MR MOHAMED SILMI NAWAZ","PortfolioValuationCurrency":"","PortfolioTotalCurrentValue":"","PortfolioLastUpdatedDateTime":"20160727"},"PortfolioHoldingId":"************060","PortfolioHoldingName":"Statement Savings","PortfolioHoldingAcctCategory":"Cash","PortfolioHoldingAcctSubCategory":"Cash_Savings","PortfolioHoldingValuationCurrency":"SGD","PortfolioHoldingLastUpdatedDateTime":"20160727","PortfolioHoldingTotalCurrentValue":"1174.86","PortfolioHoldingsAssetList":{}}
     */

    public List<AccountsObject> accounts;

    public static PdvAccounts objectFromData(String str) {

        return new Gson().fromJson(str, PdvAccounts.class);
    }

    public static PdvAccounts objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), PdvAccounts.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<PdvAccounts> arrayPdvAccountsFromData(String str) {

        Type listType = new TypeToken<ArrayList<PdvAccounts>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<PdvAccounts> arrayPdvAccountsFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<PdvAccounts>>() {
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
    }
}
