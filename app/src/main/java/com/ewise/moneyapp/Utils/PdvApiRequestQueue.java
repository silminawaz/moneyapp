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
import java.util.List;

/**
 * Created by SilmiNawaz on 8/3/17.
 */
public class PdvApiRequestQueue {
    private static String TAG = "PdvApiRequestQueue";

    private List<PdvApiRequestParams> queue;

    public PdvApiRequestQueue (){
        queue = new ArrayList<>();
    }


    public synchronized void add (PdvApiRequestParams requestParams){
        //check if there is already a request pending for this institution
        if (requestParams.updateParams!=null) {
            boolean addRequest = true;
            List<String> instIds = requestParams.updateParams.instIds;
            if (instIds!= null) {
                String instId = instIds.get(0);
                PdvApiRequestParams p = getPendingRequestForInstitution(instId);
                if (p==null) {
                    requestParams.pdvApiStatus = PdvApiStatus.PDV_API_STATUS_NOTSTARTED;
                    queue.add(requestParams);
                }
            }
        }
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

    public synchronized boolean setRequestStatus(String uuid, PdvApiStatus status){
        for (PdvApiRequestParams p : queue){
            if (p.getUuid().equals(uuid)){
                synchronized (this){
                    p.pdvApiStatus = status;
                }
                return true;
            }
        }
        return false;
    }

    public synchronized boolean stopSync(String instId, PdvApi pdvApi, PdvAcaBoundService service, Context context){
        PdvApiRequestParams p = getPendingRequestForInstitution (instId);
        PdvApiStatus status = p.pdvApiStatus;
        boolean returnValue = false;
        synchronized (this){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_NOTSTARTED)){
                queue.remove(p);
                returnValue=true;
                String apiName = PdvApiName.STOP.toString();
                Response<String> response = new Response("success", (String)null, "Stop successful.", "Stop successful.");
                String stringResponse = PdvApiResults.toJsonString(response);

                //todo: remove debug logging
                Log.d ("PdvAcaBoundService", "sendBroadcastMessage apiName:" + apiName);
                Log.d ("PdvAcaBoundService", "sendBroadcastMessage stringResponse:" + stringResponse);

                Intent intent = new Intent("pdv-aca-stop-callback");
                intent.putExtra("apiName", apiName);
                intent.putExtra("stringResponse", stringResponse);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            else{
                //request is in progress.. we can inform the service to stop it
                service.stopPendingRequest(pdvApi);
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
            p.results = results;
            return true;
        }
        return false;
    }

    //remove the head of the queue
    public synchronized PdvApiRequestParams removeHead()
    {
        PdvApiRequestParams p = null;
        if (queue.size()>0){
            p = queue.remove(0);
        }
        return p;
    }

    //remove item in position (index)
    public synchronized PdvApiRequestParams remove(int index)
    {
        PdvApiRequestParams p = null;
        if (queue.size()>index){
            p = queue.remove(index);
        }
        return p;
    }

    //remove all items of a specific status
    public synchronized boolean remove(PdvApiStatus status){
        boolean returnValue = false;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(status)){
                queue.remove(p);
                returnValue = true;
            }
        }
        return returnValue;
    }

    public PdvApiRequestParams peek()
    {
        PdvApiRequestParams p = null;
        if (queue.size()>0)
        {
            p = queue.get(0);
        }
        return p;
    }

    public int size (){
        return queue.size();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public PdvApiRequestParams peekAt(int index){
        PdvApiRequestParams p = null;
        if (index > 0 && queue.size()>index)
        {
            p = queue.get(index);
        }
        return p;
    }



    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = Resources.getSystem().getString(R.string.exception_format_string);
        Log.e(TAG, String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
