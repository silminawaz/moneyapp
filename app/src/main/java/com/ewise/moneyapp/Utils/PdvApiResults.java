package com.ewise.moneyapp.Utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.android.pdv.api.model.response.GetPromptsResponse;
import com.ewise.android.pdv.api.model.response.GetUserProfileResponse;
import com.ewise.android.pdv.api.model.response.RemoveInstitutionResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 21/2/17.
 * common result data model for any PdvApi call responses
 * static methods can be used to convert to and from Json
 */
public class PdvApiResults {

    public PdvApiName      pdvApiName;
    public int      timeoutInterval = 0;
    public boolean  callBackData = false;
    public boolean  callBackCompleted = false;
    public boolean  callBackAllComplete = false;
    private boolean  callBackPrompts = false;
    private boolean  mustShowOTP=false;
    public boolean  callBackError = false;
    public boolean  requestStopped = false;
    public boolean  timeout = false;
    private String requestUUID;


    public boolean setCallBackPrompts(boolean value){
        callBackPrompts=value;
        mustShowOTP=value;

        return callBackPrompts;
    }

    public boolean isCallBackPrompts() {
        return callBackPrompts;
    }

    public boolean setOTPDone (){
        if(callBackPrompts) {
            mustShowOTP = false;
        }

        return mustShowOTP;
    }

    public boolean isMustShowOTP() {
        return mustShowOTP;
    }

    public String getRequestUUID() {
        return requestUUID;
    }

    public void setRequestUUID(String requestUUID) {
        this.requestUUID = requestUUID;
    }

    //response for setUser() API
    public Response                 setUserResponse = null;

    //response status for initialise() API
    public String                   initialiseStatus = "";

    //response for getInstitutions() API
    public Response<Providers>      providers = null;

    //response for getUserProfile() API
    public GetUserProfileResponse   userProfile = null;

    //response for getPrompts() API
    public GetPromptsResponse       prompts = null;

    //response for updateAccounts(), restoreAccounts() APIs
    public AccountsResponse         accounts = null;

    //response for updateTransactions(), restoreTransactions() APIs
    public TransactionsResponse     transactions = null;

    //response for getCredential()
    public Response<GetPromptsData> credential = null;

    //response for setCredential()
    public Response<String>         setCredentialResponse = null;

    //response for removeInstitutions
    public RemoveInstitutionResponse removeInstitutionResponse = null;

    //response for Stop() API
    public Response<String>         stopResponse = null;

    //return common response object
    public Response getResponse (){
        Log.d("PdvApiResults", "PdvApiResults.getResponse() - start");

        Response<String> response = new Response<String>(null, null, null, null);

        if (stopResponse!=null){
            Log.d("PdvApiResults", "PdvApiResults.getResponse() - stopResponse");
            response.setStatus(stopResponse.getStatus());
            response.setErrorType(stopResponse.getErrorType());
            response.setMessage(stopResponse.getMessage());
            return response;
        }

        if (accounts!=null) {
            Log.d("PdvApiResults", "PdvApiResults.getResponse() - accounts");
            response.setStatus(accounts.getStatus());
            response.setErrorType(accounts.getErrorType());
            response.setMessage(accounts.getMessage());
            return response;
        }

        if (transactions!=null) {
            Log.d("PdvApiResults", "PdvApiResults.getResponse() - transactions");
            response.setStatus(transactions.getStatus());
            response.setErrorType(transactions.getErrorType());
            response.setMessage(transactions.getMessage());
            return response;
        }

        return null;
    }

    public boolean isPartialResponse(){
        if (accounts!=null){
            return accounts.getPartial();
        }
        else if (transactions!=null){
            return transactions.getPartial();
        }
        return false;
    }


    public String getMessageText(Context context){
        String getMessageText="";
        Response r = getResponse();
        if (r != null) {
            if (r.getStatus()!=null && r.getStatus().equals(StatusCode.STATUS_ERROR)){
                getMessageText=r.getErrorType();
            }
            else {
                if (r.getStatus()!=null && r.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                    if (isPartialResponse()) {
                        getMessageText=context.getString(R.string.provider_message_partial_response_text) + " " + r.getErrorType() + " " + r.getMessage();
                    } else {
                        getMessageText=r.getMessage();
                    }
                }
                else{
                    getMessageText=r.getMessage();
                }
            }
        }

        return getMessageText;

    }

    public static String toJsonString(Object dataObject){
        Gson gson = new Gson();
        String jsonString="";
        try {
            if (dataObject != null) {
                jsonString = gson.toJson(dataObject);
            }
        }
        catch (Exception e)
        {
            String sMethod = "PdvApiResults.toJsonString():";
            sMethod = Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            String sFormat =  "***EXCEPTION*** encountered in ***Type*** %1$s ***Method*** %2$s ***Error*** %3$s ***Object*** %4$s";
            String eObjectString = (dataObject == null ? "" : dataObject.toString());
            Log.e("GeneralException", String.format(sFormat, e.getClass().getName(), sMethod, e.getMessage(), eObjectString));
            return jsonString;

        }
        return jsonString;
    }

    public static  <T> T objectFromString(String jsonString, Class<T> type ){

        T object = new Gson().fromJson(jsonString, type);
        Log.d("CONVERT", object.toString());
        return object;
    }

    public static LinearLayout getViewsForPromptsEntry (PromptEntry promptEntry, LinearLayout layout, int fieldStartIndex, LinearLayout.LayoutParams layoutParams, Context context){

        int type = promptEntry.getType();
        switch (type){
            case 0: //text
                TextView label = new TextView(context);
                label.setLayoutParams(layoutParams);
                label.setText(promptEntry.getValue());
                layout.addView(label, fieldStartIndex);
                fieldStartIndex++;

                EditText text = new EditText(context);
                text.setLayoutParams(layoutParams);
                text.setMaxLines(1);
                text.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                text.setTag(promptEntry);
                layout.addView(text, fieldStartIndex);
                break;

            case 1: //password
                TextView pwdLabel = new TextView(context);
                pwdLabel.setLayoutParams(layoutParams);
                pwdLabel.setText(promptEntry.getValue());
                layout.addView(pwdLabel, fieldStartIndex);
                fieldStartIndex++;

                EditText pwd = new EditText(context);
                pwd.setLayoutParams(layoutParams);
                pwd.setMaxLines(1);
                pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                pwd.setTag(promptEntry);
                layout.addView(pwd, fieldStartIndex);
                break;
            case 2: //checkbox
                CheckBox checkbox = new CheckBox(context);
                checkbox.setLayoutParams(layoutParams);
                checkbox.setText(promptEntry.getValue());
                checkbox.setTag(promptEntry);
                layout.addView(checkbox, fieldStartIndex);
                break;
            default:
                TextView labelDefault = new TextView(context);
                labelDefault.setLayoutParams(layoutParams);
                labelDefault.setText(promptEntry.getValue());
                layout.addView(labelDefault, fieldStartIndex);
                fieldStartIndex++;

                EditText textDefault = new EditText(context);
                textDefault.setLayoutParams(layoutParams);
                textDefault.setMaxLines(1);
                textDefault.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
                textDefault.setTag(promptEntry);
                layout.addView(textDefault, fieldStartIndex);
                break;
        }

        return layout;
    }


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat =  Resources.getSystem().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

}