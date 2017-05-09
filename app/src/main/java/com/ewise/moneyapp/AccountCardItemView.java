package com.ewise.moneyapp;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewise.moneyapp.Utils.CurrencyAmount;
import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;


/**
 * Copyright (c) 2017 eWise Singapore. Created  on 31/8/16.
 */
@EViewGroup(R.layout.account_card_layout)
public class AccountCardItemView extends RecyclerViewItemLayoutView<AccountCardDataObject> implements RecyclerViewBindInterface<AccountCardDataObject> {

    @ViewById(R.id.acctcard_title)
    TextView acctcard_title;

    @ViewById(R.id.acctcard_numaccts)
    TextView acctcard_numaccts;

    @ViewById(R.id.acctcard_currency)
    TextView acctcard_currency;

    @ViewById(R.id.acctcard_total)
    TextView acctcard_total;

    @ViewById(R.id.acctcard_menu_btn)
    ImageView acctcard_menu_btn;

    //define the recycler view here....
    @ViewById(R.id.accountlist_recycler_view)
    RecyclerView accountlist_recycler_view;

    AccountsObjectViewAdapter accountsObjectViewAdapter;


    public AccountCardItemView(Context context){
        super(context);
    }

    public void bind (AccountCardDataObject accountCard){

        Log.d("**TRACE**", String.format("Binding Account Card  : %s", accountCard.toString()));

        acctcard_title.setText(accountCard.title);
        acctcard_numaccts.setText(accountCard.numAccounts);
        acctcard_currency.setText(accountCard.preferredCurrencyCode);
        acctcard_total.setText(CurrencyAmount.getFormattedAmount(accountCard.preferredCurrencyBalance.doubleValue(), accountCard.preferredCurrencyCode));

        //initialise and bind child RecyclerView layout
        //Attach adapter and load the data
        accountlist_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        accountsObjectViewAdapter = new AccountsObjectViewAdapter(this.getContext());
        accountlist_recycler_view.setAdapter(accountsObjectViewAdapter);
        accountsObjectViewAdapter.swapData(accountCard.accountList);

        this.acctcard_menu_btn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                accountlist_recycler_view.setVisibility(accountlist_recycler_view.isShown() ? View.GONE : View.VISIBLE);
            }
        });
    }

}
