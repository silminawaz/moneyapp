package com.ewise.moneyapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvApiStatus;

public class PdvAcaBoundService extends Service {

    private final IBinder pdvAcaServiceBinder = new PdvAcaServiceBinder();

    public class PdvAcaServiceBinder extends Binder {
        public PdvAcaBoundService getService(){
            return PdvAcaBoundService.this;
        }
    }

    public PdvAcaBoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return pdvAcaServiceBinder;
    }

    public void updateAccounts (final PdvApi pdvApi, final PdvApiRequestParams requestParams){
        final PdvApiResults results = new PdvApiResults();
        results.pdvApiName = requestParams.pdvApiName;
        results.setRequestUUID(requestParams.getUuid());

        Runnable runPdvUpdateAccounts = new Runnable() {
            @Override
            public void run() {

                try
                {
                    //call the API
                    Log.d("UpdateAccountRequest", "Starting runPdvUpdateAccountsNew runnable");
                    final PdvApiRequestQueue requestQueue = ((MoneyAppApp)getApplication()).pdvApiRequestQueue;
                    requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_INPROGRESS);
                    pdvApi.updateAccounts(requestParams.updateParams.instIds, new PdvApiCallback.PdvApiAccountsCallback() {
                        @Override
                        public void result(AccountsResponse accountsResponse) {
                            if (accountsResponse.getStatus().equals(StatusCode.STATUS_DATA)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = data");
                                results.accounts = accountsResponse;
                                results.callBackData = true;
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_DATA, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = complete");
                                results.accounts = accountsResponse;
                                results.callBackCompleted = true;
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = error");
                                results.callBackError = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = verify");
                                results.callBackPrompts = true;
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_VERIFY, requestParams, results);
                            }
                        }
                    }, requestParams.updateParams.profileId, requestParams.updateParams.credPrompts);
                }
                catch (Exception e)
                {
                    String sMethod = this.toString();
                    sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
                    String sObject = "";
                    if (requestParams!=null){
                        sObject = " ***Object*** requestParams=" + PdvApiResults.toJsonString(requestParams);;
                    }
                    if (results!=null){
                        sObject = sObject + " ***Object*** pdvApiResults=" + PdvApiResults.toJsonString(results);
                    }
                    generalExceptionHandler(e.getClass().getName(),e.getMessage(),sMethod,sObject);
                }

            }
        };

        new Thread (runPdvUpdateAccounts).start();
    }


    private void sendBroadcastCallbackResults (PdvApiName apiName, String callbackStatus, PdvApiRequestParams requestParams, PdvApiResults results){
        String sRequestParams = (requestParams != null ? PdvApiResults.toJsonString(requestParams) : null);
        String sResults = (results != null ? PdvApiResults.toJsonString(results) : null);

        //todo: remove debug logging
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage apiName:" + apiName);
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage callbackStatus:" + callbackStatus);
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage requestParams:" + sRequestParams);
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage results:" + sResults);

        Intent intent = new Intent("pdv-aca-bound-service-callback");
        intent.putExtra("apiName", apiName);
        intent.putExtra("callbackStatus", callbackStatus);
        if (requestParams!=null) { intent.putExtra("requestParams", sRequestParams); }
        if (results != null) { intent.putExtra("results", sResults);}
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getApplicationContext().getString(R.string.exception_format_string);
        Log.e("PdvAcaBoundService", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
