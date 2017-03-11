package com.ewise.moneyapp.Utils;

import android.content.res.Resources;
import android.util.Log;

import com.ewise.moneyapp.R;

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


    public void add (PdvApiRequestParams requestParams){
        requestParams.pdvApiStatus = PdvApiStatus.PDV_API_STATUS_NOTSTARTED;
        queue.add(requestParams);
    }

    public PdvApiRequestParams getNextRequestToExecute(){
        PdvApiRequestParams returnValue = null;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_NOTSTARTED)){
                returnValue = p;
            }
        }
        return returnValue;
    }

    public boolean isRequestInProgress(){
        boolean returnValue = false;
        for (PdvApiRequestParams p : queue){
            if (p.pdvApiStatus.equals(PdvApiStatus.PDV_API_STATUS_INPROGRESS)){
                return true;
            }
        }
        return false;
    }

    public boolean setRequestStatus(String uuid, PdvApiStatus status){
        for (PdvApiRequestParams p : queue){
            if (p.getUuid().equals(uuid)){
                p.pdvApiStatus = status;
                return true;
            }
        }
        return false;
    }

    public PdvApiRequestParams getRequestParams(String uuid){
        for (PdvApiRequestParams p : queue){
            if (p.getUuid().equals(uuid)){
                return p;
            }
        }
        return null;
    }

    public boolean setRequestResults(PdvApiResults results){
        PdvApiRequestParams p = getRequestParams(results.getRequestUUID());
        if (p!=null){
            p.results = results;
            return true;
        }
        return false;
    }

    //remove the head of the queue
    public PdvApiRequestParams removeHead()
    {
        PdvApiRequestParams p = null;
        if (queue.size()>0){
            p = queue.remove(0);
        }
        return p;
    }

    //remove item in position (index)
    public PdvApiRequestParams remove(int index)
    {
        PdvApiRequestParams p = null;
        if (queue.size()>index){
            p = queue.remove(index);
        }
        return p;
    }

    //remove all items of a specific status
    public boolean remove(PdvApiStatus status){
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
