package com.ewise.moneyapp;

import android.content.Context;

import android.support.v7.widget.CardView;
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

    @ViewById(R.id.account_transaction_cardview)
    CardView account_transaction_cardview;

    @ViewById(R.id.transactioncard_date)
    TextView transactioncard_date;

    @ViewById(R.id.transactioncard_cashflow)
    TextView transactioncard_cashflow;


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

            switch (cardDataObject.groupTransactionsBy){
                case DAY:
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getContext().getString(R.string.api_transaction_date_format), Locale.getDefault());
                    this.transactioncard_date.setText(dateFormat.format(cardDataObject.transactionDate));
                    break;
                case MONTH:
                    this.transactioncard_date.setText(cardDataObject.transactionMonth);
                    break;
                case YEAR:
                    this.transactioncard_date.setText(cardDataObject.transactionYear);
            }


            Log.d("***TRACE***", String.format("Date: %s | Cashin : %f | Cashout : %f", transactioncard_date.getText(), cardDataObject.totalCashIn.doubleValue(),cardDataObject.totalCashOut.doubleValue()));

            BigDecimal cashflowAmount = cardDataObject.totalCashIn.add(cardDataObject.totalCashOut);
            Log.d("***TRACE***", String.format("Date: %s | Cashflow : %f", transactioncard_date.getText(), cashflowAmount.doubleValue()));

            this.transactioncard_cashflow.setText(String.format(Locale.getDefault(), "%f", cashflowAmount.doubleValue()));

            //initialise and bind child RecyclerView layout
            //Attach adapter and load the data
            transactionlist_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
            transactionsObjectViewAdapter = new TransactionsObjectViewAdapter(this.getContext());
            transactionlist_recycler_view.setAdapter(transactionsObjectViewAdapter);
            transactionsObjectViewAdapter.swapData(cardDataObject.transactionList);

            this.

            transactionlist_recycler_view.setVisibility(cardDataObject._transactionVisibility);

            Log.d("**TRACE 2**", String.format("Binding Transaction Card : %s", cardDataObject.toString()));


            this.account_transaction_cardview.setOnClickListener(new OnClickListener(){
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

    public void toggleCardItemVisibility(){
        transactionlist_recycler_view.setVisibility(transactionlist_recycler_view.isShown() ? View.GONE : View.VISIBLE);
    }

    public void hideCardItems(){
        transactionlist_recycler_view.setVisibility(View.GONE);
    }

    public void showCardItems(){
        transactionlist_recycler_view.setVisibility(View.VISIBLE);
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getContext().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


}
