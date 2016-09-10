package com.ewise.moneyapp;

import android.content.Context;
import android.view.ViewGroup;

import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by SilmiNawaz on 4/9/16.
 */

@EBean
public class AccountCardsViewAdapter extends RecyclerViewAdapterBase<AccountCardDataObject, AccountCardItemView> {


        @RootContext
        Context context;

        public AccountCardsViewAdapter (Context context){
            this.context = context;
        }

        @Override
        protected AccountCardItemView onCreateItemView(ViewGroup parent, int viewType) {
            return AccountCardItemView_.build(context);
        }

    /*
    @Override
    public void onBindViewHolder(RecyclerViewWrapper<CurrencyExchangeRateItemView> viewHolder, int position) {
        CurrencyExchangeRateItemView view = viewHolder.getView();
        CurrencyExchangeRates.ExchangeRateItemsObject exchangeRateItem = items.get(position);

        view.bind(exchangeRateItem);
    }
    */

}
