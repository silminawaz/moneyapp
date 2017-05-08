package com.ewise.moneyapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 10/9/16.
 */

@EBean
public class TransactionCardsViewAdapter extends RecyclerViewAdapterBase<TransactionCardDataObject, TransactionCardItemView> {


    @RootContext
    Context context;

    private int _transactionVisibility = View.GONE;

    public TransactionCardsViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected TransactionCardItemView onCreateItemView(ViewGroup parent, int viewType) {
        return TransactionCardItemView_.build(context);
    }

    public int get_showTransactions(){
        return _transactionVisibility;
    }

    public void set_showTransactions(int viewVisbility){
        this._transactionVisibility = viewVisbility;
        for (TransactionCardDataObject d:items
             ) {
            d._transactionVisibility = viewVisbility;
        }
        notifyDataSetChanged();
    }

    public List<TransactionCardDataObject> getItemList(){
        return this.items;
    }

    public TransactionCardDataObject getItem(int position){
        return this.items.get(position);
    }

    @Override
    public void swapData(List<TransactionCardDataObject> newData) {
        super.swapData(newData);
        set_showTransactions (View.GONE);
    }
}
