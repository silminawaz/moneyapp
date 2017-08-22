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
import android.text.format.DateUtils;
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
import com.ewise.moneyapp.data.PdvAccountResponse;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PdvAcaBoundService extends Service {

    private final IBinder pdvAcaServiceBinder = new PdvAcaServiceBinder();
    private HashMap <String, Runnable> runnablePool = null;
    private HandlerThread handlerThread = null;
    private Date lastRefreshedDateTime = null;

    private void setLastRefreshedDateTime(){
        if (lastRefreshedDateTime==null) {
            lastRefreshedDateTime = new Date();
        }
        else{
            lastRefreshedDateTime.setTime(System.currentTimeMillis());
        }
    }

    public Date getLastRefreshedDateTime(){
        return lastRefreshedDateTime;
    }

    public boolean isRefreshedAfter(Date checkDateTime){
        if (lastRefreshedDateTime==null) return false;
        if (lastRefreshedDateTime.after(checkDateTime)){
            return true;
        }
        return false;
    }

    public class PdvAcaServiceBinder extends Binder {
        public PdvAcaBoundService getService(){
            return PdvAcaBoundService.this;
        }
    }

    public PdvAcaBoundService() {
        super();

    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("PdvApiBoundService", "onCreate() - START");
        runnablePool = new HashMap<String, Runnable>();
        handlerThread = new HandlerThread("eWisePdvApiRunThread");
        handlerThread.start();
        Log.d("PdvApiBoundService", "onCreate() - END");

    }

    @Override
    public void onDestroy(){
        Log.d("PdvApiBoundService", "onDestroy() - START");
        Looper looper = waitAndGetLooper();
        if (looper!=null){
            Handler handler = new Handler(looper);
            Iterator it = runnablePool.entrySet().iterator();
            while (it.hasNext()){
                handler.removeCallbacks((Runnable)((Map.Entry) it.next()).getValue());
            }
        }
        handlerThread.quit();
        handlerThread = null;
        runnablePool.clear();
        runnablePool = null;
        Log.d("PdvApiBoundService", "onDestroy() - END");
    }

    public HashMap<String, Runnable> getRunnablePool(){
        if (this.runnablePool == null){
            runnablePool = new HashMap<String, Runnable>();
        }

        return runnablePool;

    }

    private HandlerThread getHandlerThread(){
        if (handlerThread==null){
            Log.d("PdvApiBoundService", "getHandlerThread() - handlerThread is NULL");
            handlerThread = new HandlerThread("eWisePdvApiRunThread");
        }
        else
        {
            Log.d("PdvApiBoundService", "getHandlerThread() - handlerThread is NOT NULL");
            if (handlerThread.getState() == Thread.State.NEW)
            {
                Log.d("PdvApiBoundService", "getHandlerThread().start() - about to execute");
                handlerThread.start();
            }

        }
        return handlerThread;
    }

    private Looper waitAndGetLooper(){
        Looper looper = getHandlerThread().getLooper();
        long waitTime = 500;
        while (looper==null){
            try {
                synchronized (this) {
                    Log.d("PdvAcaBoundService", "Waiting for looper to be available. wait time = " + Long.toString(waitTime));
                    wait(waitTime);
                    looper = getHandlerThread().getLooper();
                    waitTime += 500;
                    if (waitTime>5000) break;
                }
            } catch (InterruptedException e) {
                Log.d("PdvAcaBoundService", e.getMessage());
                e.printStackTrace();
            }
        }
        return looper;
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


        Looper looper = waitAndGetLooper();

        if (looper!=null) {
            Handler handler = new Handler(looper);
            handler.post(runPdvApiStop);

            //errors - lets try and kill the runnable
            Runnable runnable = getRunnablePool().get(requestUUID);
            handler.removeCallbacks(runnable);
            getRunnablePool().remove(requestUUID);
        }
        else
        {
            sendBroadcastServiceError(getResources().getString(R.string.service_execution_error_title_stop_pending_request), getResources().getString(R.string.service_execution_error_message_looper_null));
        }
        //new Thread(runPdvApiStop).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return pdvAcaServiceBinder;
    }

    //DO NOT USE THIS API.. ITS A VERY INEFFICIENT API WITH MULTIPLE "DATA" status callbacks
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
                            //Log.d("accountsResponse", PdvApiResults.toJsonString(accountsResponse));
                            if (accountsResponse.getStatus().equals(StatusCode.STATUS_DATA)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = data");
                                results.accounts = accountsResponse;
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
                                results.accounts = accountsResponse;
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = error");
                                results.accounts = accountsResponse;
                                results.callBackError = true;
                                requestQueue.setRequestResults(results);
                                setLastRefreshedDateTime();
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (accountsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateAccountRequest", "updateAccounts() response = verify");
                                results.accounts = accountsResponse;
                                results.setCallBackPrompts(true);
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

        getRunnablePool().put(requestParams.getUuid(), runPdvUpdateAccounts);

        //call the API
        Looper looper = waitAndGetLooper();
        if (looper!=null) {
            Handler handler = new Handler(looper);
            if (handler != null) {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is not null - running updateAccounts");
                handler.post(runPdvUpdateAccounts);
            } else {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is NULL - cannot run updateAccounts on the handler thread");

            }
        }
        else{
            String title = getResources().getString(R.string.service_execution_error_title_update_account_request);
            String.format(title, requestParams.updateParams.instIds.get(0));
            sendBroadcastServiceError(title, getResources().getString(R.string.service_execution_error_message_looper_null));
        }
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
                            //Log.d("transactionsResponse=", PdvApiResults.toJsonString(transactionsResponse));
                            if (transactionsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = complete");
                                results.transactions = transactionsResponse;
                                results.callBackCompleted = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                ((MoneyAppApp)getApplication()).pdvAccountResponse.resetAccountsRefreshed();
                                setLastRefreshedDateTime();
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = error");
                                results.transactions = transactionsResponse;
                                results.callBackError = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = verify");
                                results.transactions = transactionsResponse;
                                results.setCallBackPrompts(true);
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


        getRunnablePool().put(requestParams.getUuid(), runPdvUpdateTransactionsNew);

        //call the API
        Looper looper = waitAndGetLooper();
        if (looper!=null) {
            Handler handler = new Handler(looper);
            if (handler != null) {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is not null - running updateAccounts");
                handler.post(runPdvUpdateTransactionsNew);
            } else {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is NULL - cannot run updateTransactions on the handler thread");

            }
        }
        else{
            String title = getResources().getString(R.string.service_execution_error_title_update_transactions_request);
            String.format(title, requestParams.updateParams.instIds.get(0));
            sendBroadcastServiceError(title, getResources().getString(R.string.service_execution_error_message_looper_null));
        }

        //new Thread(runPdvUpdateTransactionsNew).start();
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
                            //Log.d("transactionsResponse=", PdvApiResults.toJsonString(transactionsResponse));
                            if (transactionsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = complete");
                                results.transactions = transactionsResponse;
                                results.callBackCompleted = true;
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = all complete");
                                results.callBackAllComplete = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                setLastRefreshedDateTime();
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ALL_COMPLETE, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = error");
                                results.transactions = transactionsResponse;
                                results.callBackError = true;
                                requestQueue.setRequestStatus(requestParams.getUuid(), PdvApiStatus.PDV_API_STATUS_COMPLETED);
                                requestQueue.setRequestResults(results);
                                sendBroadcastCallbackResults(requestParams.pdvApiName, StatusCode.STATUS_ERROR, requestParams, results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                Log.d("UpdateTransactions", "UpdateTransactions() response status = verify");
                                results.transactions = transactionsResponse;
                                results.setCallBackPrompts(true);
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

        getRunnablePool().put(requestParams.getUuid(), runPdvUpdateTransactions);

        //call the API
        Looper looper = waitAndGetLooper();
        if (looper!=null) {

            Handler handler = new Handler(looper);
            if (handler != null) {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is NOT NULL - about to run runPdvUpdateTransactions on the handler thread");
                handler.post(runPdvUpdateTransactions);

            } else {
                Log.d("PdvAcaBoundService", "runPdvUpdateAccounts - handler is NULL - CANNOT run runPdvUpdateTransactions on the handler thread");

            }
        }
        else{
            String title = getResources().getString(R.string.service_execution_error_title_update_transactions_request);
            String.format(title, requestParams.updateParams.instIds.get(0));
            sendBroadcastServiceError(title, getResources().getString(R.string.service_execution_error_message_looper_null));
        }
        //Thread thread = new Thread (runPdvUpdateAccounts).start();
    }



    private void sendBroadcastCallbackResults (PdvApiName apiName, String callbackStatus, PdvApiRequestParams requestParams, PdvApiResults results){
        String sRequestParams = (requestParams != null ? PdvApiResults.toJsonString(requestParams) : null);
        String sResults = (results != null ? PdvApiResults.toJsonString(results) : null);

        //todo: remove debug logging
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage apiName:" + apiName);
        Log.d ("PdvAcaBoundService", "sendBroadcastMessage callbackStatus:" + callbackStatus);
        //Log.d ("PdvAcaBoundService", "sendBroadcastMessage requestParams:" + sRequestParams);
        //Log.d ("PdvAcaBoundService", "sendBroadcastMessage results:" + sResults);

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
        //Log.d ("PdvAcaBoundService", "sendBroadcastMessage results:" + stringResults);

        Intent intent = new Intent("pdv-aca-stop-callback");
        intent.putExtra("stringResults", stringResults);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void sendBroadcastServiceError (String title, String message){

        //todo: remove debug logging
        Log.d ("PdvAcaBoundService", "sendBroadcastServiceError method:" + title);
        Log.d ("PdvAcaBoundService", "sendBroadcastServiceError message:" + message);


        Intent intent = new Intent("pdv-aca-service-error");
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getApplicationContext().getString(R.string.exception_format_string);
        Log.e("PdvAcaBoundService", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
