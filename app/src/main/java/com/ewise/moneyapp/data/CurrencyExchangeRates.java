package com.ewise.moneyapp.data;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 28/8/16.
 */

@Keep
public class CurrencyExchangeRates {
    /**
     * baseCurrencyCode : SGD
     * quoteCurrencyCode : USD
     * exchangeRate : 0.7500
     * rateQuoteDirect : true
     */

    private static final String TAG="CurrencyExchangeRates";

    //implement as a singleton
    private static CurrencyExchangeRates ourInstance = new CurrencyExchangeRates();

    public static CurrencyExchangeRates getInstance() {
        return ourInstance;
    }

    private CurrencyExchangeRates() {

        exchangeRatesForCurrencyPairs = new HashMap<>();
    }

    public List<ExchangeRateItemsObject> exchangeRateItems;

    private HashMap<String, ExchangeRateItemsObject> exchangeRatesForCurrencyPairs;


    public HashMap<String, ExchangeRateItemsObject> getExchangeRatesForCurrencyPairs (){

        //todo: retrieve this from a service later - for now we retrieve hardcoded currencypairs

        return exchangeRatesForCurrencyPairs;
    }

    public BigDecimal exchangeAmountToCurrency (String toCurrency, String fromCurrency, BigDecimal amount){
//        Currency accountCurr = Currency.getInstance(account.currency);
        MathContext mc = new MathContext(7, RoundingMode.HALF_UP);
        BigDecimal convertedAmount= new BigDecimal(amount.toString(), mc);

        String currencyPair = toCurrency+fromCurrency;
        ExchangeRateItemsObject exchangeRate = exchangeRatesForCurrencyPairs.get(currencyPair);
        if (exchangeRate==null){
            currencyPair = fromCurrency+toCurrency;
            exchangeRate = exchangeRatesForCurrencyPairs.get(currencyPair);
        }
        if (exchangeRate!=null){
            BigDecimal rate = new BigDecimal(exchangeRate.exchangeRate, mc);
            if (exchangeRate.rateQuoteDirect){
                //Most rates will be quoted this way...
                //"SGDUSD"=0.75; "USDSGD"=1.40
                //Pair=SGDUSD:0.75 (direct); fromCurrency=USD, toCurrency=SGD, Amount=100; convertedAmount=100/0.75 = 140 SGD
                //Pair=SGDUSD:0.75 (direct); fromCurrency=SGD, toCurrency=USD, Amount=100; convertedAmount=100*0.75 = 75 USD
                //Pair=USDSGD:1.40 (direct); fromCurrency=SGD, toCurrency=USD, Amount=100; convertedAmount=100/1.40 = 75 USD
                //Pair=USDSGD:1.40 (direct); fromCurrency=USD, toCurrency=SGD, Amount=100; convertedAmount=100*1.40 = 140 SGD
                if (fromCurrency.equals(exchangeRate.baseCurrencyCode)){
                    convertedAmount=convertedAmount.multiply(rate);
                    //return amount.multiply(rate);
                }
                else{
                    convertedAmount=convertedAmount.divide(rate, BigDecimal.ROUND_HALF_DOWN);
                    //return amount.divide(rate);
                }
            }
            else
            {
                //this is now rare, but can happen if we extract currency pairs from a treasury system
                //"SGDUSD"=1.40; "USDSGD"=0.75 (Rates are usually quoted this way for few currencies like GBP)
                //Pair=SGDUSD:1.40 (indirect); fromCurrency=USD, toCurrency=SGD, Amount=100; convertedAmount=100*1.40 = 140 SGD
                //Pair=SGDUSD:1.40 (indirect); fromCurrency=SGD, toCurrency=USD, Amount=100; convertedAmount=100/1.40 = 0.75 USD
                //Pair=USDSGD:0.75 (indirect); fromCurrency=SGD, toCurrency=USD, Amount=100; convertedAmount=100*0.75 = 75 USD
                //Pair=USDSGD:0.75 (indirect); fromCurrency=USD, toCurrency=SGD, Amount=100; convertedAmount=100/0.75 = 140 SGD
                if (toCurrency.equals(exchangeRate.baseCurrencyCode)){
                    convertedAmount=convertedAmount.multiply(rate);
                    //return amount.multiply(rate);
                }
                else{
                    convertedAmount=convertedAmount.divide(rate, BigDecimal.ROUND_HALF_DOWN);
                    //return amount.divide(rate);
                }
            }
            Log.d(TAG, "From Amount = "+fromCurrency+" "+amount.toString()+ " | Exchange Rate="+exchangeRate.exchangeRate+ " | Converted amount = "+toCurrency+" "+convertedAmount.toString());
        }

        return convertedAmount;

    }


    public boolean loadExchangeRatesFromJsonAssets(Context context) {

        try {
            //open file
            String fileName = "CurrencyExchangeRates.json";
            AssetManager assetManager = context.getAssets();
            InputStream input = assetManager.open(fileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input));
            StringBuilder jsonDataStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                jsonDataStrBuilder.append(inputStr);
            }

            JSONObject jsonObject = new JSONObject(jsonDataStrBuilder.toString());

            CurrencyExchangeRatesObject currencyExchangeRatesObject = CurrencyExchangeRatesObject.objectFromData(jsonObject.toString());

            for (CurrencyExchangeRatesObject.ExchangeRateItemsObject rateItemsObject: currencyExchangeRatesObject.exchangeRateItems) {
                ExchangeRateItemsObject rate = new ExchangeRateItemsObject();
                rate.baseCurrencyCode=rateItemsObject.currencyPair.substring(0,2);
                rate.quoteCurrencyCode = rateItemsObject.currencyPair.substring(3,5);
                rate.exchangeRate = rateItemsObject.exchangeRate;
                rate.rateQuoteDirect = rateItemsObject.rateQuoteDirect;
                exchangeRatesForCurrencyPairs.put(rateItemsObject.currencyPair, rate);
            }

            return true;
        }
        //todo: better exception handling
        catch (JSONException | IOException e) {
            Log.e("EXCEPTION", e.getMessage());
            e.printStackTrace();
        }

        return false;
    }


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
        public String currencyPair;
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
