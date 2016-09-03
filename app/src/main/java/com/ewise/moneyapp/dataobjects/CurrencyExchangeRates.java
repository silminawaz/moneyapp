package com.ewise.moneyapp.dataobjects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class CurrencyExchangeRates {


    public final static CurrencyExchangeRates INSTANCE = new CurrencyExchangeRates();

    private ArrayList<CurrencyExchangeRateItem> rateItemArrayList = new ArrayList<CurrencyExchangeRateItem>();

    protected CurrencyExchangeRates() {
        // Exists only to defeat instantiation.
    }

    //load rates
    public int loadExchangeRates () {

        //todo: get currency rates from data source


        //todo: temp: dummy rates load
        BigDecimal rate = new BigDecimal("1.0");
        CurrencyExchangeRateItem rateItem = new CurrencyExchangeRateItem(Currency.getInstance("SGD"), Currency.getInstance("SGD"), true, rate);

        return 0;

    }


    public CurrencyExchangeRateItem getCurrencyRatesForCurrencyPair (String baseCurrencyCode, String quoteCurrencyCode){
        for (CurrencyExchangeRateItem rateItem : this.rateItemArrayList) {

        }
    }


}
