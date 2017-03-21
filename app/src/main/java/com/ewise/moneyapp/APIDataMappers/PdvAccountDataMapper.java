package com.ewise.moneyapp.APIDataMappers;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.ewise.android.pdv.api.model.AccountEntry;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvAccountResponse;

/**
 * Created by SilmiNawaz on 21/3/17.
 */
public class PdvAccountDataMapper {

    public PdvAccountDataMapper(){

    }

    public static PdvAccountResponse.AccountsObject getMappedObject(AccountEntry entry, Context context){

        if (entry!=null) {
            PdvAccountResponse.AccountsObject object = new PdvAccountResponse.AccountsObject();
            object.instId = entry.getInstId();
            object.accountHash = entry.getAccountHash();
            object.accountId = entry.getAccountId();
            object.accountName = entry.getAccountName();
            object.accountNumber = entry.getAccountNumber();
            object.availBalance = entry.getAvailBalance();
            object.balance = entry.getBalance();
            object.category = PdvAccountDataMapper.getAccountCategoryString(entry.getCategory(), context);
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
