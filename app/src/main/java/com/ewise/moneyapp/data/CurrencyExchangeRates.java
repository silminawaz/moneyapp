package com.ewise.moneyapp.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class CurrencyExchangeRates {
    /**
     * baseCurrencyCode : SGD
     * quoteCurrencyCode : USD
     * exchangeRate : 0.7500
     * rateQuoteDirect : true
     */

    //implement as a singleton
    private static CurrencyExchangeRates ourInstance = new CurrencyExchangeRates();

    public static CurrencyExchangeRates getInstance() {
        return ourInstance;
    }

    private CurrencyExchangeRates() {
    }

    public List<ExchangeRateItemsObject> exchangeRateItems;

    public static CurrencyExchangeRates objectFromData(String str) {


        return new Gson().fromJson(str, CurrencyExchangeRates.class);
    }

    public static CurrencyExchangeRates objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), CurrencyExchangeRates.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<CurrencyExchangeRates> arrayCurrencyExchangeRatesFromData(String str) {

        Type listType = new TypeToken<ArrayList<CurrencyExchangeRates>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<CurrencyExchangeRates> arrayCurrencyExchangeRatesFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<CurrencyExchangeRates>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }


    public static class ExchangeRateItemsObject {
        public String baseCurrencyCode;
        public String quoteCurrencyCode;
        public String exchangeRate;
        public boolean rateQuoteDirect;

        public static ExchangeRateItemsObject objectFromData(String str) {

            return new Gson().fromJson(str, ExchangeRateItemsObject.class);
        }

        public static ExchangeRateItemsObject objectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);

                return new Gson().fromJson(jsonObject.getString(str), ExchangeRateItemsObject.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        public static List<ExchangeRateItemsObject> arrayExchangeRateItemsObjectFromData(String str) {

            Type listType = new TypeToken<ArrayList<ExchangeRateItemsObject>>() {
            }.getType();

            return new Gson().fromJson(str, listType);
        }

        public static List<ExchangeRateItemsObject> arrayExchangeRateItemsObjectFromData(String str, String key) {

            try {
                JSONObject jsonObject = new JSONObject(str);
                Type listType = new TypeToken<ArrayList<ExchangeRateItemsObject>>() {
                }.getType();

                return new Gson().fromJson(jsonObject.getString(str), listType);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new ArrayList();


        }
    }




}
