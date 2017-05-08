package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.service.PdvAcaBoundService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 8/3/17.
 */
public class PdvApiRequestQueue {
    private static String TAG = "PdvApiRequestQueue";

    private List<PdvApiRequestParams> queue;
    private boolean mustRestoreAccounts = true;

    public boolean getAccountsMustBeRestored(){
        return mustRestoreAccounts;
    }

    public void resetAccountsMustBeRestored(){
        synchronized (this){
            mustRestoreAccounts=false;
        }
    }

    public PdvApiRequestQueue (){
        queue = new ArrayList<>();
    }


    public synchronized boolean add (PdvApiRequestParams requestParams){
        //check if there is already a request pending for this institution
        boolean addRequest = false;
        String instId = null;
        if (requestParams.updateParams!=null) {
            List<String> instIds = requestParams.updateParams.instIds;
            if (instIds!= null) {
                instId = instIds.get(0);
                PdvApiRequestParams p = getPendingRequestForInstitution(instId);
                if (p==null) {
                    synchronized (this) {
                        requestParams.pdvApiStatus = PdvApiStatus.PDV_API_STATUS_NOTSTARTED;
                        queue.add(requestParams);
                        addRequest=true;
                    }
                }
            }
        }
        if (addRequest){
            //clear old requests that are completed
            removeCompletedRequestForInstitution(instId);
        }

        return addRequest;
    }

    public synchronized PdvApiRequestParams getPendingRequestForInstitution (String instId){

        for (PdvApiRequestParams p : queue) {
            List<String> instIdList = p.updateParams.instIds;
            for (String instIdString : instIdList) {
                if (instIdString.equals(instId)){
                    //check status...
                    if (!p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_COMPLETED))
                    {
                        return p;
                    }
                }
            }
        }

        return null;
    }


    public synchronized int removeCompletedRequestForInstitution (String instId){

        int returnValue = 0;
        Iterator<PdvApiRequestParams> iterator = queue.iterator();
        while (iterator.hasNext()){
            PdvApiRequestParams p = iterator.next();
            List<String> instIdList = p.updateParams.instIds;
            for (String instIdString : instIdList) {
                if (instIdString.equals(instId)){
                    //check status...
                    if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_COMPLETED))
                    {
                        synchronized (this) {
                            iterator.remove();
                            returnValue++;
                        }
                    }
                }
            }
        }

        return returnValue;
    }



    public synchronized PdvApiRequestParams getRequestForInstitution (String instId){
        for (PdvApiRequestParams p : queue) {
            List<String> instIdList = p.updateParams.instIds;
            for (String instIdString : instIdList) {
                if (instIdString.equals(instId)){
                    Log.d("PdvApiRequestQueue", "getRequestForInstitution() - found requestParam for iid " + PdvApiResults.toJsonString(p));
                    return p;
                }
            }
        }

        return null;
    }

    public synchronized boolean setVerify(String instId){
        return getRequestForInstitution(instId).results.setOTPDone();
    }


    public synchronized PdvApiRequestParams getNextRequestToExecute(){
        PdvApiRequestParams returnValue = null;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_NOTSTARTED)){
                returnValue = p;
            }
        }
        return returnValue;
    }

    public synchronized boolean isRequestInProgress(){
        boolean returnValue = false;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_INPROGRESS)){
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isRequestPending(){
        boolean returnValue = false;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_INPROGRESS) || p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_NOTSTARTED)){
                return true;
            }
        }
        return false;
    }

    public synchronized PdvApiRequestParams setRequestStatus(String uuid, PdvApiStatus status){
        for (PdvApiRequestParams p : queue){
            if (p.getUuid().equals(uuid)){
                synchronized (this){
                    p.pdvApiStatus = status;
                    if (status.equals(PdvApiStatus.PDV_API_STATUS_COMPLETED)){
                        mustRestoreAccounts = true;
                    }
                }
                return p;
            }
        }
        return null;
    }

    public synchronized boolean stopSync(String instId, PdvApi pdvApi, PdvAcaBoundService service, Context context){
        PdvApiRequestParams p = getPendingRequestForInstitution (instId);
        PdvApiStatus status = p.pdvApiStatus;
        boolean returnValue = false;
        synchronized (this){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_NOTSTARTED)){
                p.pdvApiStatus=PdvApiStatus.PDV_API_STATUS_COMPLETED;
                returnValue=true;
                String apiName = PdvApiName.STOP.toString();
                Response<String> response = new Response("success", (String)null, "Stop successful.", "Stop successful.");
                PdvApiResults results = new PdvApiResults();
                results.setRequestUUID(p.getUuid());
                results.requestStopped=true;
                results.stopResponse = response;
                p.results= results;
                String stringResults = PdvApiResults.toJsonString(results);

                //todo: remove debug logging
                Intent intent = new Intent("pdv-aca-stop-callback");

                intent.putExtra("stringResults", stringResults);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            else{
                //request is in progress.. we can inform the service to stop it
                service.stopPendingRequest(pdvApi, p.getUuid());
                returnValue=true;
            }
        }

        return returnValue;
    }


    public PdvApiRequestParams getRequestParams(String uuid){
        for (PdvApiRequestParams p : queue){
            if (p.getUuid().equals(uuid)){
                return p;
            }
        }
        return null;
    }



    public synchronized boolean setRequestResults(PdvApiResults results){
        PdvApiRequestParams p = getRequestParams(results.getRequestUUID());
        if (p!=null){
            synchronized (this) {
                results.pdvApiName = p.pdvApiName;
                p.results = results;
            }
            return true;
        }
        return false;
    }


    public int size (){
        return queue.size();
    }




    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = Resources.getSystem().getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
