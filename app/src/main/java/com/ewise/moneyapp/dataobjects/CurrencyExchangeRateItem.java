package com.ewise.moneyapp.dataobjects;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class CurrencyExchangeRateItem {
    public Currency buyCurrency;
    public Currency sellCurrency;
    public BigDecimal exchangeRate;

    public CurrencyExchangeRateItem(Currency buyCurrency, Currency sellCurrency, BigDecimal exchangeRate){
        this.buyCurrency = buyCurrency;
        this.sellCurrency = sellCurrency;
        this.exchangeRate = exchangeRate;
    }

}
