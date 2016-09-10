package com.ewise.moneyapp;

import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.views.RecyclerViewBindInterface;
import com.ewise.moneyapp.views.RecyclerViewItemLayoutView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by SilmiNawaz on 7/9/16.
 */


@EViewGroup(R.layout.transationobject_item)
public class TransactionsObjectItemView extends RecyclerViewItemLayoutView<TransactionsObject> implements RecyclerViewBindInterface<TransactionsObject> {

    @ViewById(R.id.transaction_category_icon)
    ImageView transaction_category_icon;

    @ViewById(R.id.transaction_description)
    TextView transaction_description;

    @ViewById(R.id.transaction_amount)
    TextView transaction_amount;

    public TransactionsObjectItemView(Context context) {
        super(context);
    }

    public void bind(final TransactionsObject dataObject) {

        Log.d("**TRACE**", String.format("Binding Transaction Object  : %s", dataObject.toString()));

        //set on click on the icon
        this.transaction_category_icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "RecycleView Click transaction category icon" + dataObject.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        this.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {

                startTransactionDetailsActivity (dataObject);
            }
        });


        this.transaction_description.setText(dataObject.description);
        //TODO: Format balance correctly
        this.transaction_amount.setText(String.format("%f",dataObject.amount));

    }

    public void startTransactionDetailsActivity(TransactionsObject transaction){


        Toast.makeText(getContext(), "launch TransactionDetailsActivity : " + transaction.toString(), Toast.LENGTH_SHORT).show();

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
