#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import ${DATAPACKAGE}.${DATAOBJECT};

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

#parse("File Header.java")

/**
 * Created by SilmiNawaz on 5/9/16.
 */
public class ${NAME} extends AsyncTaskLoader<${DATAOBJECT}> {

    public ${NAME} (Context context){
        super(context);

    }

    @Override
    protected void onStartLoading() {
//        super.onStartLoading();

        forceLoad();  //force loadInBackground
    }

    @Override
    public ${DATAOBJECT} loadInBackground() {

        try
        {
            //open file
            String fileName = "${JSON_ASSET_FILENAME}";
            AssetManager assetManager = getContext().getAssets();
            InputStream input = assetManager.open(fileName);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input));
            StringBuilder jsonDataStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                jsonDataStrBuilder.append(inputStr);
            }

            JSONObject jsonObject = new JSONObject(jsonDataStrBuilder.toString());

            ${DATAOBJECT} object = ${DATAOBJECT}.objectFromData(jsonObject.toString());

            return object;
        }
        //todo: better exception handling
        catch (JSONException e)
        {
            Log.e("EXCEPTION",e.getMessage());
        }
        catch (IOException e)
        {
            Log.e("EXCEPTION",e.getMessage());
        }

        return null;
    }

    @Override
    public void deliverResult(${DATAOBJECT} data) {
        super.deliverResult(data);
    }
}
