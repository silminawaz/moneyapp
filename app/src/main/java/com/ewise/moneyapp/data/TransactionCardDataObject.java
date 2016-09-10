package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.loaders.PdvAccountTransactionResponseLoader;
import com.google.gson.Gson;


/**
 * Created by SilmiNawaz on 9/9/16.
 * A TransactionCardDataObject maintains a list of transactions for an account
 */
public class TransactionCardDataObject {

    public Context context = null;
    public AccountsObject account = null;
    public List<TransactionsObject> transactionList = new ArrayList<>();

    public Date transactionDate;
    public BigDecimal totalCashIn;
    public BigDecimal totalCashOut;

    public TransactionCardDataObject (Context context, AccountsObject account, Date transactionDate){

        this.context = context;
        this.account = account;
        this.transactionDate = transactionDate;

        //set the cashin and cashout values
        Currency currency = Currency.getInstance(account.currency);
        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        double dZero = 0.0;
        this.totalCashIn = new BigDecimal(dZero, mc);
        this.totalCashOut =  new BigDecimal(dZero, mc);

    }

    public void addTransaction (TransactionsObject transaction){
        Log.d("**TXN**ADD", String.format("Adding transaction : %s", transaction.toString()));

        transactionList.add(transaction);
        addTransactionToTotals (transaction);
    }

    private void addTransactionToTotals (TransactionsObject transaction){

        try {
            Currency currency = Currency.getInstance(account.currency);
            MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            BigDecimal transactionAmount = new BigDecimal("0.0", mc);
            transactionAmount = transactionAmount.add(new BigDecimal(transaction.amount, mc));
            if (transactionAmount.doubleValue()>0) {this.totalCashIn.add(transactionAmount);} else {this.totalCashOut.add(transactionAmount);}
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
