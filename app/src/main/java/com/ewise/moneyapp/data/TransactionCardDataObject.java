package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.loaders.PdvAccountTransactionResponseLoader;
import com.google.gson.Gson;


/**
 * Copyright (c) 2017 eWise Singapore. Created  on 9/9/16.
 * A TransactionCardDataObject maintains a list of transactions for an account
 */
public class TransactionCardDataObject {

    public Context context = null;
    public AccountsObject account = null;
    public List<TransactionsObject> transactionList = new ArrayList<>();


    public enum eGroupTransactionsBy {
        DAY,
        MONTH,
        YEAR
    }

    public String transactionYear;
    public String transactionMonth;
    public Date transactionDate;
    public BigDecimal totalCashIn;
    public BigDecimal totalCashOut;
    public eGroupTransactionsBy groupTransactionsBy = eGroupTransactionsBy.DAY;

    public int _transactionVisibility = View.GONE;

    public TransactionCardDataObject (Context context, AccountsObject account, Date transactionDate, eGroupTransactionsBy groupTransactionsBy){

        this.context = context;
        this.account = account;
        this.transactionDate = transactionDate;

        this.transactionMonth = new SimpleDateFormat(context.getString(R.string.transaction_groupby_month_format), Locale.getDefault()).format(transactionDate);
        Log.d("**TRACE**", String.format("transactionMonth = : %s", transactionMonth));
        this.transactionYear = new SimpleDateFormat(context.getString(R.string.transaction_groupby_year_format), Locale.getDefault()).format(transactionDate);

        this.groupTransactionsBy = groupTransactionsBy;

        //set the cashin and cashout values
        Currency currency = Currency.getInstance(account.currency);
        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        double dZero = 0.0;
        this.totalCashIn = new BigDecimal(dZero, mc);
        this.totalCashOut =  new BigDecimal(dZero, mc);

    }

    public void addTransaction (TransactionsObject transaction){
        Log.d("***TRACE***", String.format("Adding transaction : %s", transaction.toString()));

        transactionList.add(transaction);
        addTransactionToTotals (transaction);
    }

    private void addTransactionToTotals (TransactionsObject transaction){

        try {
            Currency currency = Currency.getInstance(account.currency);
            MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            BigDecimal transactionAmount = BigDecimal.valueOf(transaction.amount);
            if (transactionAmount.compareTo(BigDecimal.ZERO)<0) {
                this.totalCashOut = this.totalCashOut.add(transactionAmount);
                Log.d("***TRACE***", String.format("Date: %s | TransactionAmount : %f | Cashout : %f", transaction.date, transactionAmount.doubleValue(), totalCashOut.doubleValue()));

            } else {
                this.totalCashIn = this.totalCashIn.add(transactionAmount);
                Log.d("***TRACE***", String.format("Date: %s | TransactionAmount : %f | Cashin : %f", transaction.date, transactionAmount.doubleValue(), totalCashIn.doubleValue()));
            }
        }
        catch (Exception e)
        {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(),e.getMessage(),sMethod,transactionList.toString());
        }

    }

    public void clearTransactions()
    {
        this.transactionList.clear();
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


}
