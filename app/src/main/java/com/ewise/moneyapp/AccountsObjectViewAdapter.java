package com.ewise.moneyapp;

import android.content.Context;
import android.view.ViewGroup;

import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 7/9/16.
 */

@EBean
public class AccountsObjectViewAdapter extends RecyclerViewAdapterBase<AccountsObject, AccountsObjectItemView> {


    @RootContext
    Context context;

    public AccountsObjectViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected AccountsObjectItemView onCreateItemView(ViewGroup parent, int viewType) {
        return AccountsObjectItemView_.build(context);
    }

    /*
    @Override
    public void onBindViewHolder(RecyclerViewWrapper<AccountsObjectItemView> viewHolder, int position) {
        viewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Recycle Click", Toast.LENGTH_SHORT).show();
            }
        });
        super.onBindViewHolder(viewHolder, position);
    }
*/

}
