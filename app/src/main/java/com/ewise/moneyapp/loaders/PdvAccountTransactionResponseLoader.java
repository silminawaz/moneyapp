package com.ewise.moneyapp.loaders;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.data.PdvAccountResponse.AccountsObject;
import com.ewise.moneyapp.data.PdvTransactionResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SilmiNawaz on 9/9/16.
 */

public class PdvAccountTransactionResponseLoader extends AsyncTaskLoader<PdvTransactionResponse> {

    private AccountsObject _account = null;

    /**
     * Use this constructor to load transactions for all accounts
    * */
    public PdvAccountTransactionResponseLoader(Context context) {
        super(context);

    }

    /**
     * Use this constructor to load transactions for a specific accounts
     * */
    public PdvAccountTransactionResponseLoader(Context context, AccountsObject account) {
        super(context);

        _account = account;

    }

    @Override
    protected void onStartLoading() {
//        super.onStartLoading();

        forceLoad();  //force loadInBackground
    }

    @Override
    public PdvTransactionResponse loadInBackground() {

        try {

            //TODO: Change loaders to support loading data via PDV or Aegis APIs instead of from canned data assets
            //open file
            String fileName = "PdvCashTransactionResponse.json";
            if (_account != null) {
                if (_account.category.equals(R.string.pdv_account_category_CREDIT)) {
                    fileName = "PdvCreditTransactionresponse.json";
                }
            }
            AssetManager assetManager = getContext().getAssets();
            InputStream input = assetManager.open(fileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input));
            StringBuilder jsonDataStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                jsonDataStrBuilder.append(inputStr);
            }

            JSONObject jsonObject = new JSONObject(jsonDataStrBuilder.toString());

            PdvTransactionResponse object = PdvTransactionResponse.objectFromData(jsonObject.toString());

            Log.d("**TXN**", String.format("Transactions loaded : %s", object.toString()));

            return object;
        }
        //todo: better exception handling
        catch (JSONException e) {
            Log.e("EXCEPTION", e.getMessage());
        } catch (IOException e) {
            Log.e("EXCEPTION", e.getMessage());
        }

        return null;
    }

    @Override
    public void deliverResult(PdvTransactionResponse data) {
        super.deliverResult(data);
    }
}
