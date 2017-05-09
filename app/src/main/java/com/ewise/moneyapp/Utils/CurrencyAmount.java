package com.ewise.moneyapp.Utils;

import com.ewise.moneyapp.data.CurrencyExchangeRates;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Copyright (c) 2017 eWise Singapore.
 * Created on 9/5/17.
 */
public class CurrencyAmount {


    public static String getFormattedAmount(double amount, String currencyCode){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        Currency currency = Currency.getInstance(currencyCode);
        numberFormat.setMaximumFractionDigits(currency.getDefaultFractionDigits());
        numberFormat.setMinimumFractionDigits(currency.getDefaultFractionDigits());
        return numberFormat.format(amount);
    }


    public static String getFormattedAmount(String amount, String currencyCode){
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        double dAmount = 0.0;
        if (amount!=null && !amount.isEmpty()) {
             dAmount = Double.parseDouble(amount);
        }
        return getFormattedAmount(dAmount, currencyCode);
    }

    public static double exchangeCurrency(String fromCurrency, double fromAmount, String toCurrency){
        BigDecimal amount=new BigDecimal(fromAmount);
        CurrencyExchangeRates rates = CurrencyExchangeRates.getInstance();
        BigDecimal toAmount = rates.exchangeAmountToCurrency(toCurrency, fromCurrency, amount);
        return toAmount.doubleValue();
    }

}
