package com.ewise.moneyapp;

import android.content.Context;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.data.TransactionCardDataObject;

import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

/**
 * Created by SilmiNawaz on 10/9/16.
 */


@EViewGroup(R.layout.account_transaction_card_layout)
public class TransactionCardItemView extends RecyclerViewItemLayoutView<TransactionCardDataObject> implements RecyclerViewBindInterface<TransactionCardDataObject> {

    @ViewById(R.id.transactioncard_date)
    TextView transactioncard_date;

    @ViewById(R.id.transactioncard_cashflow)
    TextView transactioncard_cashflow;

    @ViewById(R.id.transactioncard_menu_btn)
    ImageView transactioncard_menu_btn;

    @ViewById(R.id.transactionlist_recycler_view)
    RecyclerView transactionlist_recycler_view;

    TransactionsObjectViewAdapter transactionsObjectViewAdapter;

    TransactionCardDataObject cardDataObject;

    public TransactionCardItemView(Context context) {
        super(context);
    }

    public void bind(TransactionCardDataObject cardDataObject) {
        try
        {
            cardDataObject = cardDataObject;

            Log.d("**TRACE 1**", String.format("Binding Transaction Card  : %s", cardDataObject.transactionDate.toString()));


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH);
            this.transactioncard_date.setText(dateFormat.format(cardDataObject.transactionDate));
            BigDecimal cashflowAmount = cardDataObject.totalCashIn.subtract(cardDataObject.totalCashOut);
            this.transactioncard_cashflow.setText(cashflowAmount.toString());

            //initialise and bind child RecyclerView layout
            //Attach adapter and load the data
            transactionlist_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
            transactionsObjectViewAdapter = new TransactionsObjectViewAdapter(this.getContext());
            transactionlist_recycler_view.setAdapter(transactionsObjectViewAdapter);
            transactionsObjectViewAdapter.swapData(cardDataObject.transactionList);


            Log.d("**TRACE 2**", String.format("Binding Transaction Card : %s", cardDataObject.toString()));


            this.transactioncard_menu_btn.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                transactionlist_recycler_view.setVisibility(transactionlist_recycler_view.isShown() ? View.GONE : View.VISIBLE);
            }
        });

            this.transactioncard_cashflow.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View view) {
                    transactionlist_recycler_view.setVisibility(transactionlist_recycler_view.isShown() ? View.GONE : View.VISIBLE);
                }
            });


        }
        catch (Exception e) {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sObjString = (this.cardDataObject == null) ? "source error object is null" : this.cardDataObject.toString();
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), sMethod, sObjString);
        }
    }



    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getContext().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


}
