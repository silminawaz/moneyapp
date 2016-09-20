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
                    Date transactionDate = new SimpleDateFormat(_context.getString(R.string.api_transaction_date_format), Locale.getDefault()).parse(transaction.date);
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
            SimpleDateFormat format = new SimpleDateFormat(_context.getString(R.string.api_transaction_date_format), Locale.getDefault());
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

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = _context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
