package com.ewise.moneyapp.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore.
 * Created on 9/5/17.
 */
public class CurrencyExchangeRatesObject {


    public List<ExchangeRateItemsObject> exchangeRateItems;

    public static CurrencyExchangeRatesObject objectFromData(String str) {

        return new Gson().fromJson(str, CurrencyExchangeRatesObject.class);
    }

    public static CurrencyExchangeRatesObject objectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);

            return new Gson().fromJson(jsonObject.getString(str), CurrencyExchangeRatesObject.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<CurrencyExchangeRatesObject> arrayCurrencyExchangeRatesObjectFromData(String str) {

        Type listType = new TypeToken<ArrayList<CurrencyExchangeRatesObject>>() {
        }.getType();

        return new Gson().fromJson(str, listType);
    }

    public static List<CurrencyExchangeRatesObject> arrayCurrencyExchangeRatesObjectFromData(String str, String key) {

        try {
            JSONObject jsonObject = new JSONObject(str);
            Type listType = new TypeToken<ArrayList<CurrencyExchangeRatesObject>>() {
            }.getType();

            return new Gson().fromJson(jsonObject.getString(str), listType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList();


    }

    public static class ExchangeRateItemsObject {
        /**
         * currencyPair : SGDSGD
         * exchangeRate : 1.0
         * rateQuoteDirect : true
         */

        public String currencyPair;
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
