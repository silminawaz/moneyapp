package com.ewise.moneyapp;

import android.content.Context;
import android.view.ViewGroup;

import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by SilmiNawaz on 10/9/16.
 */

@EBean
public class TransactionCardsViewAdapter extends RecyclerViewAdapterBase<TransactionCardDataObject, TransactionCardItemView> {


    @RootContext
    Context context;

    public TransactionCardsViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected TransactionCardItemView onCreateItemView(ViewGroup parent, int viewType) {
        return TransactionCardItemView_.build(context);
    }

}
