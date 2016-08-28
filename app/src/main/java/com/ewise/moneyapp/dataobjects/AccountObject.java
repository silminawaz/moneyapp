package com.ewise.moneyapp.dataobjects;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Created by SilmiNawaz on 23/8/16.
 * purpose: holder for an individual account object (parent class)
 *          derive from this class to implement accounts of other types
 *          or use this type for generic account functionality
 */

public class AccountObject {
    public final String id;
    public final String instId;
    public final String instDesc;
    public final String type;
    public final String number;
    public final String name;
    public final String currency;
    public final String balance;
    public final String funds;
    public final String data;  //this need to be decompiled into a POJO


    public AccountObject()
    {

        this.id = null;
        this.instId = null;
        this.instDesc = null;
        this.type = null;
        this.number = null;
        this.name = null;
        this.currency = null;
        this.balance = null;
        this.funds = null;
        this.data = null;
    }

    public AccountObject(
            String id,
            String instId,
            String instDesc,
            String type,
            String number,
            String name,
            String currency,
            String balance,
            String funds,
            String data) {

        this.id = id;
        this.instId = instId;
        this.instDesc = instDesc;
        this.type = type;
        this.number = number;
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.funds = funds;
        this.data = data;  //TODO: expand the data object from JSON into its own data object based on the account type schema

    }


    public Currency getISOCurrency() {
        return Currency.getInstance(currency);
    }

    public BigDecimal getBalanceAmount() {
        BigDecimal amount = new BigDecimal (balance);
        return amount;
    }

    public BigDecimal getFundsAmount() {
        BigDecimal amount = new BigDecimal (funds);
        return amount;
    }

    public BigDecimal getBalanceAmount(Currency ccy, CurrencyExchangeRates rates) {
        //go through the rates and find the
        BigDecimal amount = new BigDecimal (balance);
        return amount;
    }

    public BigDecimal getFundsAmount(Currency ccy, CurrencyExchangeRates rates) {
        BigDecimal amount = new BigDecimal (funds);
        return amount;
    }

    public String mystringtestonly()
    {
        return currency;
    }
}
