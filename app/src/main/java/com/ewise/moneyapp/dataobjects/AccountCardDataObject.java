package com.ewise.moneyapp.dataobjects;

import android.content.Context;

import com.ewise.moneyapp.R;

import java.util.ArrayList;
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
    private String title;

    //currency for display only
    private String displayCurrencyCode;
    private BigDecimal displayCurrencyBalance;
    private BigDecimal displayCurrencyFunds;

    //user's preferred or base currency
    private String preferredCurrencyCode;
    private BigDecimal preferredCurrencyBalance;
    private BigDecimal preferredCurrencyFunds;


    private ArrayList<AccountObject> accountList = new ArrayList<AccountObject>();

    public AccountCardDataObject(Context context, eAccountCategory category, ArrayList<AccountObject> accountList, String preferredCurrencyCode) {
        this.context = context;
        this.category = category;
        this.accountList = accountList;
        this.preferredCurrencyCode = preferredCurrencyCode;
        this.displayCurrencyCode = this.preferredCurrencyCode;
        setTitle();
        recalculateBalances();
    }

    //Set the title for the account card
    private void setTitle() {

        switch (category) {
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
        }
    }


    private void recalculateBalances() {
        this.preferredCurrencyBalance = BigDecimal.ZERO;
        this.preferredCurrencyFunds = BigDecimal.ZERO;
        this.displayCurrencyBalance = BigDecimal.ZERO;
        this.displayCurrencyFunds = BigDecimal.ZERO;
        CurrencyExchangeRates rates = CurrencyExchangeRates.INSTANCE;

        //calculate the preferred currency balance from the accountList
        for (AccountObject acct : accountList) {
            //todo: does not support multi-currency accounts - only supports all accounts in preferred/base, currency
            //find the exchange rate for account currency to preferred currency
            CurrencyExchangeRateItem rateItem = rates.
            this.preferredCurrencyBalance = this.preferredCurrencyBalance.add(acct.getBalanceAmount());
            this.preferredCurrencyFunds = this.preferredCurrencyFunds.add(acct.getFundsAmount());
        }

        changeDisplayCurrency(this.displayCurrency, )

        this.displayCurrencyBalance = this.preferredCurrencyBalance;
        this.displayCurrencyFunds = this.preferredCurrencyFunds;
    }


    // set the display currency and recalculate the display currency balances
    public boolean changePreferredCurrency(Currency preferredCurrency, CurrencyExchangeRateItem preferredCurrencyExchangeRate) {

        //check if the displayCurrency and the exchange rates are matching
        if (!preferredCurrencyExchangeRate.RateIsValidForCurrency(preferredCurrency)) return false;

        if (preferredCurrency.equals(this.preferredCurrency)) {
            return true; //nothing to do - already in that currency
        } else {
            if (preferredCurrency.equals(this.displayCurrency)) {
                this.preferredCurrencyBalance = this.displayCurrencyBalance;
                this.preferredCurrencyFunds = this.displayCurrencyFunds;
                return true;
            } else {
                //new preferred currency
                this.preferredCurrency = preferredCurrency;
                this.displayCurrencyFunds = BigDecimal.ZERO;
                this.displayCurrencyBalance = BigDecimal.ZERO;

                if (displayCurrency.equals(displayCurrencyExchangeRate.quoteCurrency)) {
                    this.displayCurrencyBalance = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyBalance);
                    this.displayCurrencyFunds = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyFunds);
                } else {
                    if (displayCurrency.equals(displayCurrencyExchangeRate.baseCurrency)) {
                        this.displayCurrencyBalance = displayCurrencyExchangeRate.ExchangeToBaseCurrency(this.preferredCurrencyBalance);
                        this.displayCurrencyFunds = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyFunds);
                    }
                }
            }
        }

        return false;
    }


    // set the display currency and recalculate the display currency balances
    public boolean changeDisplayCurrency(Currency displayCurrency) {

        CurrencyExchangeRateItem displayCurrencyExchangeRate = CurrencyExchangeRates.INSTANCE.getExchangeRateItem

        //check if the displayCurrency and the exchangerates are matching
        if (!displayCurrencyExchangeRate.RateIsValidForCurrency(displayCurrency)) return false;

        if (displayCurrency.equals(this.displayCurrency)) {
            return true; //nothing to do - already in that currency
        } else {
            if (displayCurrency.equals(this.preferredCurrency)) {
                this.displayCurrencyBalance = this.preferredCurrencyBalance;
                this.displayCurrencyFunds = this.preferredCurrencyFunds;
                return true;
            } else {
                //new display currency
                this.displayCurrency = displayCurrency;
                this.displayCurrencyFunds = BigDecimal.ZERO;
                this.displayCurrencyBalance = BigDecimal.ZERO;

                if (displayCurrency.equals(displayCurrencyExchangeRate.quoteCurrency)) {
                    this.displayCurrencyBalance = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyBalance);
                    this.displayCurrencyFunds = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyFunds);
                } else {
                    if (displayCurrency.equals(displayCurrencyExchangeRate.baseCurrency)) {
                        this.displayCurrencyBalance = displayCurrencyExchangeRate.ExchangeToBaseCurrency(this.preferredCurrencyBalance);
                        this.displayCurrencyFunds = displayCurrencyExchangeRate.ExchangeToQuoteCurrency(this.preferredCurrencyFunds);
                    }
                }
            }
        }

        return false;
    }

}

