package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.google.gson.Gson;

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
public class AccountCardDataObject {


    public enum eAccountCategory {
        UNKNOWN,
        CASH,
        CREDIT,
        LOAN,
        INVESTMENT,
        RETIREMENT,
        PROPERTY,
        INSURANCE,
        REWARDS,
        BILLS,
        OTHERASSETS,
        OTHERLIABILITIES


    }

    //private static final String TAG = "AccountsFragment";

    private Context context;
    public eAccountCategory category;
    public String title;
    public String numAccounts;

    //currency for display only
    public String displayCurrencyCode;
    public BigDecimal displayCurrencyBalance;
    public BigDecimal displayCurrencyFunds;

    //user's preferred or base currency
    public String preferredCurrencyCode;
    public BigDecimal preferredCurrencyBalance;
    public BigDecimal preferredCurrencyFunds;


    public List<AccountsObject> accountList;

    public AccountCardDataObject(Context context, eAccountCategory category, ArrayList<AccountsObject> accountList, String preferredCurrencyCode) {
        this.context = context;
        this.category = category;
        this.accountList= new ArrayList<>();
        this.accountList.addAll(accountList); //assumption: the account list is filtered and only contains accounts of this eAccountCategory
        this.preferredCurrencyCode = preferredCurrencyCode;
        this.displayCurrencyCode = this.preferredCurrencyCode; //set display currency to preferred currency
        this.numAccounts = String.format("(%d)", accountList.size());
        setTitle();
        initialise();
    }

    //Set the title for the account card
    private void setTitle() {

        switch (category) {
            case CASH:
                title = context.getString(R.string.label_account_fragment_CASH);
                return;
            case CREDIT:
                title = context.getString(R.string.label_account_fragment_CREDIT);
                return;
            case LOAN:
                title = context.getString(R.string.label_account_fragment_LOAN);
                return;
            case INVESTMENT:
                title = context.getString(R.string.label_account_fragment_INVESTMENT);
                return;
            case RETIREMENT:
                title = context.getString(R.string.label_account_fragment_RETIREMENT);
                return;
            case INSURANCE:
                title = context.getString(R.string.label_account_fragment_INSURANCE);
                return;
            case PROPERTY:
                title = context.getString(R.string.label_account_fragment_PROPERTY);
                return;
            case BILLS:
                title = context.getString(R.string.label_account_fragment_BILLS);
                return;
            case REWARDS:
                title = context.getString(R.string.label_account_fragment_REWARDS);
                return;
            case OTHERASSETS:
                title = context.getString(R.string.label_account_fragment_OTHERASSETS);
                return;
            case OTHERLIABILITIES:
                title = context.getString(R.string.label_account_fragment_OTHERLIABILITIES);
                return;
            case UNKNOWN:
                title = context.getString(R.string.label_account_fragment_UNKNOWN);
                return;
        }
    }

    //Set the title for the account card
    public String getTitle() {

        switch (category) {
            case CASH:
                return context.getString(R.string.label_account_fragment_CASH);
            case CREDIT:
                return context.getString(R.string.label_account_fragment_CREDIT);
            case LOAN:
                return context.getString(R.string.label_account_fragment_LOAN);
            case INVESTMENT:
                return context.getString(R.string.label_account_fragment_INVESTMENT);
            case RETIREMENT:
                return context.getString(R.string.label_account_fragment_RETIREMENT);
            case INSURANCE:
                return context.getString(R.string.label_account_fragment_INSURANCE);
            case PROPERTY:
                return context.getString(R.string.label_account_fragment_PROPERTY);
            case BILLS:
                return context.getString(R.string.label_account_fragment_BILLS);
            case REWARDS:
                return context.getString(R.string.label_account_fragment_REWARDS);
            case OTHERASSETS:
                return context.getString(R.string.label_account_fragment_OTHERASSETS);
            case OTHERLIABILITIES:
                return context.getString(R.string.label_account_fragment_OTHERLIABILITIES);
            case UNKNOWN:
                return context.getString(R.string.label_account_fragment_UNKNOWN);
        }

        return context.getString(R.string.label_account_fragment_UNKNOWN);

    }


    private void initialise() {

        Currency currency = Currency.getInstance(preferredCurrencyCode);
        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        double dZero = 0.0;
        this.preferredCurrencyBalance = new BigDecimal(dZero, mc);
        this.preferredCurrencyFunds =  new BigDecimal(dZero, mc);
        this.displayCurrencyBalance =  new BigDecimal(dZero, mc);
        this.displayCurrencyFunds =  new BigDecimal(dZero, mc);

        //CurrencyExchangeRates rates = CurrencyExchangeRates.INSTANCE;
        for (AccountsObject acct : accountList) {
            //find the exchange rate for account currency to preferred currency
            String acctCurrency = acct.currency;
            //if (acct.category.equals(this.category.toString())){ //really should be an ASSERT
                if (acctCurrency!=null && !acctCurrency.isEmpty()){
                    //only add the account totals if there is a real currency code
                    addAccountToTotal(acct);
                }
            //}

        }

        //TODO: add support for changing display currency later
        this.displayCurrencyBalance = this.preferredCurrencyBalance;
        this.displayCurrencyFunds = this.preferredCurrencyFunds;
    }

    public void addAccount(AccountsObject account){

        accountList.add(account);
        this.addAccountToTotal(account);

    }

    private void addAccountToTotal(AccountsObject account){


        try {

            Currency currency = Currency.getInstance(account.currency);
            MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            CurrencyExchangeRates rates = CurrencyExchangeRates.getInstance();

            String toCurrency = preferredCurrencyCode;
            String fromCurrency = preferredCurrencyCode;
            if (account.currency!=null){
                if(!account.currency.isEmpty()) {
                    fromCurrency=account.currency;
                }
            }

            //BigDecimal acctBalance = new BigDecimal("0.0", mc);
            BigDecimal acctFunds = new BigDecimal("0.0", mc);

            BigDecimal acctBalance = new BigDecimal(account.balance, mc);
            if (!fromCurrency.equals(toCurrency)) {
                acctBalance = rates.exchangeAmountToCurrency(toCurrency, fromCurrency, acctBalance);
            }
            if (account.availBalance.length()>0) {
                acctFunds = new BigDecimal(account.availBalance, mc);
                if (!fromCurrency.equals(toCurrency)) {
                    acctFunds = rates.exchangeAmountToCurrency(toCurrency, fromCurrency, acctFunds);
                }
            }
            this.preferredCurrencyBalance = BigDecimal.valueOf(this.preferredCurrencyBalance.doubleValue()).add(acctBalance);
            this.preferredCurrencyFunds = BigDecimal.valueOf(this.preferredCurrencyFunds.doubleValue()).add(acctFunds);
            this.numAccounts = String.format("(%d)", accountList.size());

            String msg =
                    "AccountCardDataObject.addAccountToTotal() - "
                    + "Card: " + this.getTitle()
                    + "| Account=" + account.accountName
                    + "| Account Balance=" + account.balance
                    + "| Card Balance = " + preferredCurrencyBalance.toString();
            Log.d("AccountCardDataObject", msg);

        }
        catch (Exception e)
        {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, account.toString());
        }
    }

    @Override
    public String toString() {
        //return super.toString();
        /* AccountsFragment crashing with this
        try {

            String jsonString = new Gson().toJson(this);

            return jsonString.toString();
        }
        catch (Exception e){

            Log.e(TAG, e.getMessage());

        }

        */

        //return PdvApiResults.toJsonString(this);

        return super.toString();
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }



}

