package com.ewise.moneyapp.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiName;
import com.ewise.moneyapp.Utils.PdvApiRequestParams;
import com.ewise.moneyapp.Utils.PdvApiRequestQueue;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvApiStatus;

import java.util.HashMap;

public class PdvAcaBoundService extends Service {

    private final IBinder pdvAcaServiceBinder = new PdvAcaServiceBinder();
    private HashMap <String, Runnable> runnablePool = null;
    private HandlerThread handlerThread = null;

    public class PdvAcaServiceBinder extends Binder {
        public PdvAcaBoundService getService(){
            return PdvAcaBoundService.this;
        }
    }

    public PdvAcaBoundService() {
        runnablePool = new HashMap<String, Runnable>();
        handlerThread = new HandlerThread("eWisePdvApiRunThread");
        handlerThread.start();
    }

    @Override
    public void onDestroy(){
        handlerThread.quitSafely();
        runnablePool.clear();
        runnablePool = null;
    }

    private HandlerThread getHandlerThread(){
        if (handlerThread==null){
            handlerThread = new HandlerThread("eWisePdvApiRunThread");
        }
        else
        {
            if (handlerThread.getLooper()==null){
                handlerThread.start();
            }
        }
        return handlerThread;
    }

    public void stopPendingRequest (final PdvApi pdvApi, final String requestUUID) {

        Runnable runPdvApiStop = new Runnable() {
            @Override
            public void run() {
                pdvApi.stop(new PdvApiCallback<String>() {
                    @Override
                    public void result(Response<String> response) {
                        PdvApiRequestQueue requestQueue = ((MoneyAppApp)getApplication()).pdvApiRequestQueue;
                        Log.d("PDVAPI-STOP", "Stop() response: " + PdvApiResults.toJsonString(response));
                        PdvApiResults results = new PdvApiResults();
                        results.setRequestUUID(requestUUID);
                        results.requestStopped=true;
                        results.stopResponse = response;
                        requestQueue.setRequestResults(results);
                        requestQueue.setRequestStatus(results.getRequestUUID(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                        sendBroadcastStopResults(results);
                    }
                });
            }
        };


        Handler handler = new Handler(getHandlerThread().getLooper());
        handler.post(runPdvApiStop);

        //errors - lets try and kill the runnable
        Runnable runnable = runnablePool.get(requestUUID);
        handler.removeCallbacks(runnable);
        runnablePool.remove(requestUUID);
        //new Thread(runPdvApiStop).start();
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
                    Log.d("PdvAcaBoundService", "Starting runPdvUpdateAccountsNew runnable");
                    final PdvApiRequestQueue requestQueue = ((MoneyAppApp)getApplication()).pdvApiRequestQueue;
                    requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_INPROGRESS);
                    pdvApi.updateAccounts(requestParams.updateParams.instIds, new PdvApiCallback.PdvApiAccountsCallback() {
                        @Override
                        public void result(AccountsResponse accountsResponse) {
                            Log.d("accountsResponse", PdvApiResults.toJsonString(accountsResponse));
                            results.accounts = accountsResponse;
                            if (accountsResponse.getStatus().equals(StatusCode.STATUS_DATA)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = data");
                                results.callBackData = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_DATA, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = complete");
                                results.callBackCompleted = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestResults(results);
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = error");
                                results.callBackError = true;
                                requestQueue.setRequestResults(results);
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = verify");
                                results.callBackPrompts = true;
                                requestQueue.setRequestResults(results);
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

        runnablePool.put(requestParams.getUuid(), runPdvUpdateAccounts);

        //call the API

        Handler handler = new Handler(getHandlerThread().getLooper());
        handler.post(runPdvUpdateAccounts);
        //Thread thread = new Thread (runPdvUpdateAccounts).start();
    }


    public void updateTransactionsNewCredentials (final PdvApi pdvApi, final PdvApiRequestParams requestParams){
        final PdvApiResults results = new PdvApiResults();
        results.pdvApiName = requestParams.pdvApiName;
        results.setRequestUUID(requestParams.getUuid());

        Runnable runPdvUpdateTransactionsNew = new Runnable() {
            @Override
            public void run() {

                try
                {
                    Log.d("PdvAcaBoundService", "Starting runPdvUpdateTransactions runnable");
                    final PdvApiRequestQueue requestQueue = ((MoneyAppApp)getApplication()).pdvApiRequestQueue;
                    requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_INPROGRESS);
                    pdvApi.updateTransactions(requestParams.updateParams.instIds, null, null, new PdvApiCallback.PdvApiTransactionsCallback() {
                        @Override
                        public void result(TransactionsResponse transactionsResponse) {
                            Log.d("transactionsResponse=", PdvApiResults.toJsonString(transactionsResponse));
                            results.transactions = transactionsResponse;
                            if (transactionsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = complete");
                                results.callBackCompleted = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = error");
                                results.callBackError = true;
                                requestQueue.setRequestResults(results);
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = verify");
                                results.callBackPrompts = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_VERIFY, requestParams, results);
                            }
                        }
                    }, requestParams.updateParams.credPrompts);
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

        runnablePool.put(requestParams.getUuid(), runPdvUpdateTransactionsNew);

        //call the API

        //Handler handler = new Handler(getHandlerThread().getLooper());
        //handler.post(runPdvUpdateTransactionsNew);
        new Thread(runPdvUpdateTransactionsNew).start();
    }

    public void updateTransactions (final PdvApi pdvApi, final PdvApiRequestParams requestParams){
        final PdvApiResults results = new PdvApiResults();
        results.pdvApiName = requestParams.pdvApiName;
        results.setRequestUUID(requestParams.getUuid());

        Runnable runPdvUpdateTransactions = new Runnable() {
            @Override
            public void run() {

                try
                {
                    Log.d("PdvAcaBoundService", "Starting runPdvUpdateTransactions runnable");
                    final PdvApiRequestQueue requestQueue = ((MoneyAppApp)getApplication()).pdvApiRequestQueue;
                    requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_INPROGRESS);
                    pdvApi.updateTransactions(requestParams.updateParams.instIds, null, null, new PdvApiCallback.PdvApiTransactionsCallback() {
                        @Override
                        public void result(TransactionsResponse transactionsResponse) {
                            Log.d("transactionsResponse=", PdvApiResults.toJsonString(transactionsResponse));
                            results.transactions = transactionsResponse;
                            if (transactionsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = complete");
                                results.callBackCompleted = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = error");
                                results.callBackError = true;
                                requestQueue.setRequestResults(results);
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = verify");
                                results.callBackPrompts = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_VERIFY, requestParams, results);
                            }
                        }
                    });
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

        runnablePool.put(requestParams.getUuid(), runPdvUpdateTransactions);

        //call the API

        Handler handler = new Handler(getHandlerThread().getLooper());
        handler.post(runPdvUpdateTransactions);
        //Thread thread = new Thread (runPdvUpdateAccounts).start();
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
        intent.putExtra("apiName", apiName.toString());
        intent.putExtra("callbackStatus", callbackStatus);
        if (requestParams!=null) { intent.putExtra("requestParams", sRequestParams); }
        if (results != null) { intent.putExtra("results", sResults);}
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendBroadcastStopResults (PdvApiResults results){

        String stringResults = PdvApiResults.toJsonString(results);

        //todo: remove debug logging
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage results:" + stringResults);

        Intent intent = new Intent("pdv-aca-stop-callback");
        intent.putExtra("stringResults", stringResults);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getApplicationContext().getString(R.string.exception_format_string);
        Log.e("PdvAcaBoundService", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
