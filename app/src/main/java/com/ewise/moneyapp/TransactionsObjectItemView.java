package com.ewise.moneyapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.moneyapp.Utils.CurrencyAmount;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 7/9/16.
 */


@EViewGroup(R.layout.transationobject_item)
public class TransactionsObjectItemView extends RecyclerViewItemLayoutView<TransactionsObject> implements RecyclerViewBindInterface<TransactionsObject> {

    @ViewById(R.id.transaction_category_icon)
    ImageView transaction_category_icon;

    @ViewById(R.id.transaction_description)
    TextView transaction_description;

    @ViewById(R.id.transaction_amount)
    TextView transaction_amount;

    @ViewById(R.id.transaction_layout)
    RelativeLayout transaction_layout;

    public TransactionsObjectItemView(Context context) {
        super(context);
    }

    public void bind(final TransactionsObject dataObject) {

        this.transaction_description.setText(dataObject.description);
        this.transaction_amount.setText(CurrencyAmount.getFormattedAmount(dataObject.amount, dataObject.currency));

        //set on click on the icon
        this.transaction_category_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "RecycleView Click transaction category icon" + dataObject.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        transaction_layout.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(), String.format("Description: %s | Amount: %s", dataObject.description, dataObject.amount), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startTransactionDetailsActivity(TransactionsObject transaction){



/*      //TODO: Implement "TransactionDetailsActivity" to view / edit transactions
        String transactionObjJson = transaction.toString();
        Intent intent= new Intent(getContext(), TransactionDetailsActivity_.class);
        intent.putExtra("com.wise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject", transactionObjJson);
        getContext().startActivity(intent);
*/
    }


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getContext().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }



}
