package com.ewise.moneyapp.APIDataMappers;

import android.content.Context;

import com.ewise.android.pdv.AccountTransactionEntry;
import com.ewise.android.pdv.api.model.AccountEntry;
import com.ewise.android.pdv.api.model.TransactionEntry;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 21/3/17.
 */
public class PdvTransactionResponseDataMapper {

    public PdvTransactionResponseDataMapper(){

    }

    public static PdvTransactionResponse getMappedObject(TransactionsResponse entry, Context context){
        if (entry!=null) {
            PdvTransactionResponse object = new PdvTransactionResponse();
            object.status = entry.getStatus();
            object.errorType = entry.getErrorType();
            object.message = entry.getMessage();
            object.instId = entry.getInstId();
            object.accountTransactions = new ArrayList<>();
            if (entry.getAccountTransactions()!=null){
                for (AccountTransactionEntry accountTransactionEntry : entry.getAccountTransactions()){
                    object.accountTransactions.add(PdvAccountTransactionsDataMapper.getMappedObject(accountTransactionEntry, context));
                }
            }

            return object;
        }
        return null;
    }


    public static String getAccountCategoryString(int accountCategoryInt, Context context){

        return PdvTransactionAccountDataMapper.getAccountCategoryString(accountCategoryInt, context);

    }


    public static class PdvAccountTransactionsDataMapper {

        public PdvAccountTransactionsDataMapper(){

        }

        public static AccountTransactionsObject getMappedObject(AccountTransactionEntry entry, Context context){
            if (entry!=null) {
                AccountTransactionsObject object = new AccountTransactionsObject();

                object.accountId = entry.getAccountId();
                object.instId = entry.getInstId();
                object.account = PdvTransactionAccountDataMapper.getMappedObject(entry.getAccount(), context);
                object.transactions = new ArrayList<>();
                List<TransactionEntry> transactionEntries = entry.getTransactions();
                for (TransactionEntry t : transactionEntries){
                    object.transactions.add(PdvTransactionsDataMapper.getMappedObject(t, context));
                }

                return object;
            }
            return null;
        }


        public static String getAccountCategoryString(int accountCategoryInt, Context context){

            return PdvTransactionAccountDataMapper.getAccountCategoryString(accountCategoryInt, context);
        }

    }


    public static class PdvTransactionAccountDataMapper {

        public PdvTransactionAccountDataMapper(){

        }

        public static AccountTransactionsObject.AccountObject getMappedObject(AccountEntry entry, Context context){

            if (entry!=null) {
                AccountTransactionsObject.AccountObject object = new AccountTransactionsObject.AccountObject();
                object.instId = entry.getInstId();
                object.accountHash = entry.getAccountHash();
                object.accountId = entry.getAccountId();
                object.accountName = entry.getAccountName();
                object.accountNumber = entry.getAccountNumber();
                object.availBalance = entry.getAvailBalance();
                object.balance = entry.getBalance();
                object.category = PdvTransactionAccountDataMapper.getAccountCategoryString(entry.getCategory(), context);
                object.currency = entry.getCurrency();
                object.data = entry.getData();

                //todo: uncomment when updatedat date/time available
                //object.updatedAt = ?

                return object;
            }

            return null;
        }


        public static String getAccountCategoryString(int accountCategoryInt, Context context){
            switch (accountCategoryInt){
                case 1: return context.getString(R.string.pdv_account_category_CASH);
                case 2: return context.getString(R.string.pdv_account_category_PROPERTY);
                case 3: return context.getString(R.string.pdv_account_category_INVESTMENT);
                case 4: return context.getString(R.string.pdv_account_category_SUPERANNUATION);
                case 5: return context.getString(R.string.pdv_account_category_EFFECTS);
                case 6: return context.getString(R.string.pdv_account_category_INSURANCE);
                case 7: return context.getString(R.string.pdv_account_category_ASSETS);
                case 8: return context.getString(R.string.pdv_account_category_CREDIT);
                case 9: return context.getString(R.string.pdv_account_category_MORTGAGE);
                case 10: return context.getString(R.string.pdv_account_category_INVESTLOAN);
                case 11: return context.getString(R.string.pdv_account_category_LOAN);
                case 12: return context.getString(R.string.pdv_account_category_TAX);
                case 13: return context.getString(R.string.pdv_account_category_OVERDRAFT);
                case 14: return context.getString(R.string.pdv_account_category_LIABILITIES);
                default: return context.getString(R.string.pdv_account_category_EFFECTS);

            }
        }

    }

    public static class PdvTransactionsDataMapper {

        public PdvTransactionsDataMapper(){

        }

        public static AccountTransactionsObject.TransactionsObject getMappedObject(TransactionEntry entry, Context context){
            if (entry!=null) {
                AccountTransactionsObject.TransactionsObject object = new AccountTransactionsObject.TransactionsObject();
                object.amount = entry.getAmount().doubleValue();
                object.currency = entry.getCurrency();
                object.data = entry.getData();
                object.date = entry.getDate();
                object.description = entry.getDescription();
                object.fingerprint = entry.getFingerprint();
                object.id = entry.getId();
                return object;
            }
            return null;
        }


        public static String getAccountCategoryString(int accountCategoryInt, Context context){

                return PdvTransactionAccountDataMapper.getAccountCategoryString(accountCategoryInt, context);

        }

    }

}
