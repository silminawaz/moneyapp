package com.ewise.moneyapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ewise.moneyapp.AccountsObjectItemView;
import com.ewise.moneyapp.AccountsObjectItemView_;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.views.RecyclerViewAdapterBase;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by SilmiNawaz on 7/9/16.
 */

@EBean
public class TransactionsObjectViewAdapter extends RecyclerViewAdapterBase<TransactionsObject, TransactionsObjectItemView> {


    @RootContext
    Context context;

    public TransactionsObjectViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected TransactionsObjectItemView onCreateItemView(ViewGroup parent, int viewType) {
        return TransactionsObjectItemView_.build(context);
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
