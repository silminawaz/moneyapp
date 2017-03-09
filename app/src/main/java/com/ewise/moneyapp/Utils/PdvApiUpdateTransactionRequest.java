package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.ewise.android.pdv.api.callbacks.PdvApiCallback;
import com.ewise.android.pdv.api.model.StatusCode;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;

/**
 * Created by SilmiNawaz on 6/3/17.
 * We need this class to prevent long running PDV API requests from leaking Activity references
 * as and when the system destroys activities and recreates them (for example when the phone is
 * turned (orientation switches from portrsit to landscape) or when the OS needs to reclaim memory
 * and does garbage collection
 */
public class PdvApiUpdateTransactionRequest extends Fragment {

    private static final String TAG = "UpdateTransactionsReq";

    private static final String ARG_REQUEST_PARAMS = "request_params";


    interface PdvApiUpdateTransactionResponseCallbacks {
        public void onPdvApiUpdateTransactionResponsePrompts(PdvApiResults results);
        public void onPdvApiUpdateTransactionResponseData(PdvApiResults results);
        public void onPdvApiUpdateTransactionResponseComplete(PdvApiResults results);
        public void onPdvApiUpdateTransactionResponseAllComplete(PdvApiResults results);
        public void onPdvApiUpdateTransactionResponseError(PdvApiResults results);
    }

    private PdvApiUpdateTransactionResponseCallbacks callbacks;
    private PdvApiRequestParams requestParams = null;
    private PdvApiResults results;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (PdvApiUpdateTransactionResponseCallbacks) context;
        results = new PdvApiResults();
    }


    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    /**
     * Returns a new instance of this fragment for the given request parameters
     */
    public static PdvApiUpdateTransactionRequest newInstance(String requestParamsString) {
        PdvApiUpdateTransactionRequest fragment = new PdvApiUpdateTransactionRequest();
        Bundle args = new Bundle();
        args.putString(ARG_REQUEST_PARAMS, requestParamsString);
        Log.d("requestParamString", requestParamsString);
        return fragment;
    }


    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        final String requestParamsString = getArguments().getString(ARG_REQUEST_PARAMS);

        requestParams = PdvApiResults.objectFromString(requestParamsString, PdvApiRequestParams.class);
        results.pdvApiName = requestParams.pdvApiName;


        Runnable runPdvUpdateTransactionsNew = new Runnable() {
            @Override
            public void run() {

                try
                {
                    //call the API
                    MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                    app.pdvApi.updateTransactions(requestParams.updateParams.instIds, null, null, new PdvApiCallback.PdvApiTransactionsCallback() {
                        @Override
                        public void result(TransactionsResponse transactionsResponse) {
                        if (transactionsResponse.getStatus().equals(StatusCode.STATUS_COMPLETE)) {
                                results.transactions = transactionsResponse;
                                results.callBackCompleted = true;
                                callbacks.onPdvApiUpdateTransactionResponseComplete(results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ALL_COMPLETE)) {
                                results.callBackAllComplete = true;
                                callbacks.onPdvApiUpdateTransactionResponseAllComplete(results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_ERROR)) {
                                results.callBackError = true;
                                callbacks.onPdvApiUpdateTransactionResponseError(results);
                            } else if (transactionsResponse.getStatus().equals(StatusCode.STATUS_VERIFY)) {
                                results.callBackPrompts = true;
                                callbacks.onPdvApiUpdateTransactionResponsePrompts(results);
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
                        sObject = " ***Object*** requestParams=" + requestParamsString;
                    }
                    if (results!=null){
                        sObject = sObject + " ***Object*** pdvApiResults=" + PdvApiResults.toJsonString(results);
                    }
                    generalExceptionHandler(e.getClass().getName(),e.getMessage(),sMethod,sObject);
                }

            }
        };

        new Thread (runPdvUpdateTransactionsNew).start();
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = getResources().getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
