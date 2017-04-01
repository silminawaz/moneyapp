package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.data.TransactionCardDataObject.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by SilmiNawaz on 9/9/16.
 * DataObject contains lists of transaction cards by date
 */
public class TransactionCardListDataObject {

    private List<TransactionCardDataObject> _transactionCardList = new ArrayList<>();

    private AccountsObject _account = null;

    private Context _context = null;

    public enum TransactionSortOrder{
        DATE_ASCENDING,
        DATE_DESCENDING
    }


    public TransactionCardListDataObject (Context context, PdvTransactionResponse transactionResponse, AccountsObject account, eGroupTransactionsBy groupTransactionsBy){
        AccountTransactionsObject accountTransactionDump = new AccountTransactionsObject();
        try {
                _context = context;
                //build the list of cards based on the transactionResponse
                for (AccountTransactionsObject accountTransaction : transactionResponse.accountTransactions) {
                    accountTransactionDump = accountTransaction;
                    //find the account
                    if (accountTransaction.accountId.equals(account.accountHash)){

                        addAccountTransactions(accountTransaction, groupTransactionsBy);
                        return;
                    }
                }
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = (accountTransactionDump == null) ? "source error object is null" : accountTransactionDump.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }
    }


    /**
     * Add all the transactions in the incoming object by creating a separate TransactionCardDataObject for each date
     * @param accountTransaction
     *
     */
    public void addAccountTransactions(AccountTransactionsObject accountTransaction, eGroupTransactionsBy groupTransactionsBy){
        try
        {
            Log.d("**TXN**ADD", String.format("Adding transactions : %s", accountTransaction.toString()));

            _account = accountTransaction.account.getAccountsObject();
            TransactionCardDataObject transactionCard=null;
            for (TransactionsObject transaction : accountTransaction.transactions){
                boolean reloadTransactionCard=false;
                if (transactionCard==null) {
                    reloadTransactionCard=true;
                }
                else {
                    //TODO: PDV response date may change to object

                    String apiDateFormat = (transaction.date.length()>8) ? ( _context.getString(R.string.api_transaction_date_format)) : ( _context.getString(R.string.api_transaction_date_format_2));
                    Date transactionDate = new SimpleDateFormat(apiDateFormat, Locale.getDefault()).parse(transaction.date);
                    switch (groupTransactionsBy) {
                        case DAY:
                            if (!transactionDate.equals(transactionCard.transactionDate)){{reloadTransactionCard=true;}}
                            break;
                        case MONTH:
                            String transactionMonth = new SimpleDateFormat(_context.getString(R.string.transaction_groupby_month_format), Locale.getDefault()).format(transactionDate);
                            if (!transactionMonth.equals(transactionCard.transactionMonth)){{reloadTransactionCard=true;}}
                            break;
                        case YEAR:
                            String transactionYear = new SimpleDateFormat(_context.getString(R.string.transaction_groupby_year_format), Locale.getDefault()).format(transactionDate);
                            if (!transactionYear.equals(transactionCard.transactionYear)){{reloadTransactionCard=true;}}
                    }
                }

               // reloadTransactionCard = true;//TODO: temp - removed once algo fixed

                if (reloadTransactionCard) {
                    transactionCard = getTransactionCard(transaction, groupTransactionsBy);
                }

                transactionCard.addTransaction(transaction);
            }
        }
        catch (Exception e){
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, accountTransaction.toString());
        }

    }

    private TransactionCardDataObject getTransactionCard (TransactionsObject transaction, eGroupTransactionsBy groupTransactionsBy){
        try
        {
            //TODO: PDV response date may change to object
            String apiDateFormat = (transaction.date.length()>8) ? ( _context.getString(R.string.api_transaction_date_format)) : ( _context.getString(R.string.api_transaction_date_format_2));
            SimpleDateFormat format = new SimpleDateFormat(apiDateFormat, Locale.getDefault());
            Date transactionDate = format.parse(transaction.date);  //TODO: transaction.date may change to an object in real api response
            for (TransactionCardDataObject transactionCard : this._transactionCardList){
                if (groupTransactionsBy == eGroupTransactionsBy.DAY) {
                    if (transactionCard.transactionDate.equals(transactionDate)) {
                        Log.d("**TXN**ADD", String.format("getTransactionCard () - Existing card : %s", transaction.toString()));
                        return transactionCard;
                    }
                }
                else if (groupTransactionsBy == eGroupTransactionsBy.MONTH) {
                    //get the transaction month
                    String transactionMonth = new SimpleDateFormat(_context.getString(R.string.transaction_groupby_month_format), Locale.getDefault()).format(transactionDate);
                    Log.d("**TRACE**", String.format("transactionCard.transactionMonth = %s | transactionMonth = O%s", transactionCard.transactionMonth, transactionMonth));

                    if (transactionCard.transactionMonth.equals(transactionMonth)) {
                        return transactionCard;
                    }
                }
                else
                {
                    //get the transaction year
                    assert groupTransactionsBy == eGroupTransactionsBy.YEAR;
                    String transactionYear = new SimpleDateFormat(_context.getString(R.string.transaction_groupby_year_format), Locale.getDefault()).format(transactionDate);
                    if (transactionCard.transactionYear.equals(transactionYear)) {
                        return transactionCard;
                    }
                }
            }
            Log.d("**TXN**ADD", String.format("getTransactionCard () New card : %s", transaction.toString()));
            TransactionCardDataObject transactionCard = new TransactionCardDataObject(_context, _account, transactionDate, groupTransactionsBy);
            _transactionCardList.add(transactionCard);
            return transactionCard;
        }
        catch (Exception e)
        {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, transaction.toString());
        }
        return null;
    }

    public List<TransactionCardDataObject> get_transactionCardList(){
        return _transactionCardList;
    }

    public List<TransactionCardDataObject> get_transactionCardList(final TransactionSortOrder sortOrder){

        List<TransactionCardDataObject> sortedList = _transactionCardList;
        if (sortOrder.equals(TransactionSortOrder.DATE_ASCENDING)) {
            Collections.sort(sortedList, new Comparator<TransactionCardDataObject>() {
                @Override
                public int compare(TransactionCardDataObject t1, TransactionCardDataObject t2) {
                    //return 0 : t1 == t2
                    //return <0 : t1 before t2 (ascending)
                    //return >0 : t1 after t2 (descending)
                    return t1.transactionDate.compareTo(t2.transactionDate);
                }
            });
        }
        else {
            Collections.sort(sortedList, new Comparator<TransactionCardDataObject>() {
                @Override
                public int compare(TransactionCardDataObject t1, TransactionCardDataObject t2) {
                    //return 0 : t1 == t2
                    //return <0 : t1 after t2 (descending)
                    //return >0 : t1 before t2 (ascending)
                    return t2.transactionDate.compareTo(t1.transactionDate);
                }
            });
        }


        return sortedList;
    }


    public static List<TransactionCardDataObject> sortTransactionList(final TransactionSortOrder sortOrder, List<TransactionCardDataObject> transactionCardList){

        List<TransactionCardDataObject> sortedList = transactionCardList;
        if (sortOrder.equals(TransactionSortOrder.DATE_ASCENDING)) {
            Collections.sort(sortedList, new Comparator<TransactionCardDataObject>() {
                @Override
                public int compare(TransactionCardDataObject t1, TransactionCardDataObject t2) {
                    //return 0 : t1 == t2
                    //return <0 : t1 before t2 (ascending)
                    //return >0 : t1 after t2 (descending)
                    return t1.transactionDate.compareTo(t2.transactionDate);
                }
            });
        }
        else {
            Collections.sort(sortedList, new Comparator<TransactionCardDataObject>() {
                @Override
                public int compare(TransactionCardDataObject t1, TransactionCardDataObject t2) {
                    //return 0 : t1 == t2
                    //return <0 : t1 after t2 (descending)
                    //return >0 : t1 before t2 (ascending)
                    return t2.transactionDate.compareTo(t1.transactionDate);
                }
            });
        }


        return sortedList;
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = _context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
