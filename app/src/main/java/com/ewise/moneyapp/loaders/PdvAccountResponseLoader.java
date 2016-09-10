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
