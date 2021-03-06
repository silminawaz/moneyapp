package com.ewise.moneyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.moneyapp.Utils.CurrencyAmount;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 7/9/16.
 */


@EViewGroup(R.layout.accountobject_item)
public class AccountsObjectItemView extends RecyclerViewItemLayoutView<AccountsObject> implements RecyclerViewBindInterface<AccountsObject> {

    @ViewById(R.id.accounticon)
    ImageView accounticon;

    @ViewById(R.id.accountname)
    TextView accountname;

    @ViewById(R.id.accountnumber)
    TextView accountnumber;

    @ViewById(R.id.accountcurrency)
    TextView accountcurrency;

    @ViewById(R.id.accountbalance)
    TextView accountbalance;

    @ViewById(R.id.accountlayout)
    RelativeLayout accountlayout;

    public interface MyCallback{
        void onItemClicked();
    }

    private MyCallback listener;


    public AccountsObjectItemView(Context context) {
        super(context);
    }

    public void setOnItemClickListener(MyCallback callback){
        listener = callback;
    }

    public void bind(final AccountsObject dataObject) {

        //set on click on the icon
        this.accounticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "RecycleView Click icon" + dataObject.toString(), Toast.LENGTH_SHORT).show();
                //listener.onItemClicked();
            }
        });
/*
        this.accountname.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                startAccountDetailsActivity (dataObject);

            }
        });
*/
        this.accountlayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startAccountDetailsActivity (dataObject);
            }
        });


        this.accountname.setText(dataObject.accountName);
        this.accountnumber.setText(dataObject.accountNumber);
        this.accountcurrency.setText(dataObject.currency);
        this.accountbalance.setText(CurrencyAmount.getFormattedAmount(dataObject.balance, dataObject.currency));
        this.accounticon.setImageResource(((MoneyAppApp) getContext().getApplicationContext()).getInstitutionIconResourceId(dataObject.instId));

    }

    public void startAccountDetailsActivity(AccountsObject account){
        //TODO: We need both account and the related transactions from the Transaction response for this account... load it up here,
        //      and use gson to serialise and send the data into the activity then retrieve it and render inside the activity.

        Activity activity = (Activity) this.getContext();
        MoneyAppApp app = (MoneyAppApp) activity.getApplication();
        if (!((MoneyAppApp) activity.getApplication()).pdvApiRequestQueue.isRequestPending()){
            String accountObjJson = account.toString();
            Intent intent= new Intent(getContext(), AccountDetailsActivity_.class);
            intent.putExtra("com.wise.moneyapp.data.PdvAccountResponse.AccountsObject", accountObjJson);
            activity.startActivityForResult(intent, MoneyAppApp.ACCOUNT_DETAILS_ACTIVITY );
        }
        else
        {
            Toast.makeText(this.getContext(), R.string.pdvapi_request_inprogress, Toast.LENGTH_SHORT).show();
        }

    }


}
