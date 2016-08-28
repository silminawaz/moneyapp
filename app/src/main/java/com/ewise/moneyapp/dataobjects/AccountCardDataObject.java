package com.ewise.moneyapp.dataobjects;

import android.content.Context;

import com.ewise.moneyapp.R;

import java.util.Collections;
import java.util.Currency;
import java.math.BigDecimal;
import java.util.List;


/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class AccountCardDataObject {


    public enum eAccountCategory {
        E_ACCOUNT_CATEGORY_CASH,
        E_ACCOUNT_CATEGORY_CREDIT,
        E_ACCOUNT_CATEGORY_LOAN,
        E_ACCOUNT_CATEGORY_INVESTMENT,
        E_ACCOUNT_CATEGORY_RETIREMENT,
        E_ACCOUNT_CATEGORY_PROPERTY,
        E_ACCOUNT_CATEGORY_INSURANCE,
        E_ACCOUNT_CATEGORY_REWARDS,
        E_ACCOUNT_CATEGORY_BILLS,
        E_ACCOUNT_CATEGORY_OTHERASSETS,
        E_ACCOUNT_CATEGORY_OTHERLIABILITIES
    }

    private Context context;
    private eAccountCategory category;
    private String title;  //todo: set title for the account card
    private Currency displayCurrency;  //todo: initialise the display currency
                                        //todo: set display currency - re calculate the balance based on that currency
    private BigDecimal displayCurrencyBalance; //todo: calculate the display currency balance
    private Currency preferredCurrency; //todo: initialise the preferred currency
    private Currency preferredCurrencyBalance; //todo: calculate the preferred currency balance

    private List<AccountObject> accountList = Collections.emptyList();

    public AccountCardDataObject (Context context, eAccountCategory category, List<AccountObject> accountList, Currency preferredCurrency){
        this.context = context;
        this.category = category;
        this.accountList = accountList;
        this.preferredCurrency = preferredCurrency;
        this.displayCurrency = this.preferredCurrency;
        setTitle ();
        setPreferredCurrencyBalance();
    }

    //Set the title for the account card
    private void setTitle (){

        switch (category)
        {
            case E_ACCOUNT_CATEGORY_CASH:
                title = context.getString(R.string.label_account_fragment_CASH);
                return;
            case E_ACCOUNT_CATEGORY_CREDIT:
                title = context.getString(R.string.label_account_fragment_CREDIT);
                return;
            case E_ACCOUNT_CATEGORY_LOAN:
                title = context.getString(R.string.label_account_fragment_LOAN);
                return;
            case E_ACCOUNT_CATEGORY_INVESTMENT:
                title = context.getString(R.string.label_account_fragment_INVESTMENT);
                return;
            case E_ACCOUNT_CATEGORY_RETIREMENT:
                title = context.getString(R.string.label_account_fragment_RETIREMENT);
                return;
            case E_ACCOUNT_CATEGORY_INSURANCE:
                title = context.getString(R.string.label_account_fragment_INSURANCE);
                return;
            case E_ACCOUNT_CATEGORY_PROPERTY:
                title = context.getString(R.string.label_account_fragment_PROPERTY);
                return;
            case E_ACCOUNT_CATEGORY_BILLS:
                title = context.getString(R.string.label_account_fragment_BILLS);
                return;
            case E_ACCOUNT_CATEGORY_REWARDS:
                title = context.getString(R.string.label_account_fragment_REWARDS);
                return;
            case E_ACCOUNT_CATEGORY_OTHERASSETS:
                title = context.getString(R.string.label_account_fragment_OTHERASSETS);
                return;
            case E_ACCOUNT_CATEGORY_OTHERLIABILITIES:
                title = context.getString(R.string.label_account_fragment_OTHERLIABILITIES);
                return;

        }
    }


    private void setPreferredCurrencyBalance ()
    {
        //calculate the preferred currency balance from the accountList


    }




}
