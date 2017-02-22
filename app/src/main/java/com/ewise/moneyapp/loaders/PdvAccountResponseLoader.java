package com.ewise.moneyapp.loaders;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.ewise.moneyapp.data.PdvAccountResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by SilmiNawaz on 5/9/16.
 */
public class PdvAccountResponseLoader extends AsyncTaskLoader<PdvAccountResponse> {

    public PdvAccountResponseLoader (Context context){
        super(context);

    }

    @Override
    protected void onStartLoading() {
//        super.onStartLoading();

        forceLoad();  //force loadInBackground
    }

    @Override
    public PdvAccountResponse loadInBackground() {

        try
        {
            //TODO: Change loaders to support loading account data via PDV or Aegis APIs instead of from canned data assets
            //      1. class in com.ewise.moneyapp.data contains the data structure classes used by the app(e.g. PdvAccountResponse)
            //      2. create a data access class in com.ewise.moenyapp.api package to access data (create, read, update, delete - data access from network via apis)
            //      3. create a data mapper class in com.ewise.moneyapp.ApiDataMapper to map between api data and app data classes (found in com.ewise.moneyapp.data package)

            //open file
            String fileName = "PdvAccountResponse.json";
            AssetManager assetManager = getContext().getAssets();
            InputStream input = assetManager.open(fileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input));
            StringBuilder jsonDataStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                jsonDataStrBuilder.append(inputStr);
            }

            JSONObject jsonObject = new JSONObject(jsonDataStrBuilder.toString());

            PdvAccountResponse object = PdvAccountResponse.objectFromData(jsonObject.toString());

            return object;
        }
        //todo: better exception handling
        catch (JSONException e)
        {
            Log.d("JSON",e.getMessage());
        }
        catch (IOException e)
        {
            Log.d("IO",e.getMessage());
        }

        return null;
    }

    @Override
    public void deliverResult(PdvAccountResponse data) {
        super.deliverResult(data);
    }
}
