package com.ewise.moneyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.data.AccountCardListDataObject;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject;
import com.ewise.moneyapp.data.PdvTransactionResponse.AccountTransactionsObject.TransactionsObject;
import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.data.TransactionCardListDataObject;
import com.ewise.moneyapp.loaders.PdvAccountResponseLoader;
import com.ewise.moneyapp.loaders.PdvAccountTransactionResponseLoader;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_account_details)
public class AccountDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<PdvTransactionResponse> {

    @ViewById(R.id.accountdetails_accountname)
    TextView accountdetails_accountname;

    @ViewById(R.id.accountdetails_accountnumber)
    TextView accountdetails_accountnumber;

    @ViewById(R.id.accountdetails_accountcurrency)
    TextView accountdetails_accountcurrency;

    @ViewById(R.id.accountdetails_accountbalance)
    TextView accountdetails_accountbalance;

    @ViewById(R.id.accountdetails_accounticon)
    ImageView accountdetails_accounticon;

    @ViewById(R.id.accountdetails_lastupdated)
    TextView accountdetails_lastupdated;

    PdvAccountResponse.AccountsObject _account = null;

    @ViewById(R.id.accountdetails_transactioncard_recycler_view)
    RecyclerView accountdetails_transactioncard_recycler_view;

    TransactionCardsViewAdapter transactionCardsViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_account_details);



    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String accountObjJson = intent.getStringExtra("com.wise.moneyapp.data.PdvAccountResponse.AccountsObject");
        _account = PdvAccountResponse.AccountsObject.objectFromData(accountObjJson);

        accountdetails_accountname.setText(_account.accountName);
        accountdetails_accountnumber.setText(_account.accountNumber);
        accountdetails_accountbalance.setText(_account.balance);
        accountdetails_accountcurrency.setText(_account.currency);
        accountdetails_lastupdated.setText(_account.updatedAt);

        //Attach adapter and load the data
        accountdetails_transactioncard_recycler_view.setLayoutManager(new LinearLayoutManager(AccountDetailsActivity.this));
        transactionCardsViewAdapter = new TransactionCardsViewAdapter(AccountDetailsActivity.this);
        accountdetails_transactioncard_recycler_view.setAdapter(transactionCardsViewAdapter);

        getSupportLoaderManager().initLoader(R.id.PdvTransactionResponse_Loader_id,null,this);
    }

    @Override
    protected void onResume() {

        super.onResume();


    }

    public void onClose(View view)
    {
        this.finish();
    }

    @Override
    public Loader<PdvTransactionResponse> onCreateLoader(int id, Bundle args) {
        return new PdvAccountTransactionResponseLoader(AccountDetailsActivity.this, _account);
    }


    @Override
    public void onLoadFinished(Loader<PdvTransactionResponse> loader, PdvTransactionResponse data) {

        Log.d("**TXN**LOAD***", String.format("Callback fired: AccountDetailsActivity.onLoadFinished() : %s", data.toString()));

        TransactionCardListDataObject transactionList = new TransactionCardListDataObject(getApplicationContext(), data, _account);
        transactionCardsViewAdapter.swapData(transactionList.get_transactionCardList());
//        synchronized (transactionCardsViewAdapter) {
//            transactionCardsViewAdapter.notifyAll();
//        }
        Log.d("**TRACE:NOTIFY ALL***", "Callback fired: Notify changed");

    }

    @Override
    public void onLoaderReset(Loader<PdvTransactionResponse> loader) {
        transactionCardsViewAdapter.resetData();
    }

    /**
     * LoaderManager callback routine for PdvTransactionResponse data
     */
    private LoaderManager.LoaderCallbacks<PdvTransactionResponse> loaderCallbacks = new LoaderManager.LoaderCallbacks<PdvTransactionResponse>(){

        @Override
        public Loader<PdvTransactionResponse> onCreateLoader(int id, Bundle args) {
            return new PdvAccountTransactionResponseLoader(AccountDetailsActivity.this, _account);
        }


        @Override
        public void onLoadFinished(Loader<PdvTransactionResponse> loader, PdvTransactionResponse data) {

            TransactionCardListDataObject transactionList = new TransactionCardListDataObject(getApplicationContext(), data, _account);
            transactionCardsViewAdapter.swapData(transactionList.get_transactionCardList());
        }

        @Override
        public void onLoaderReset(Loader<PdvTransactionResponse> loader) {
            transactionCardsViewAdapter.resetData();
        }

    };
}
