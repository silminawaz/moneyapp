package com.ewise.moneyapp.Utils;

/**
 * Created by SilmiNawaz on 8/3/17.
 */

public interface PdvApiGetNextRequestFromQueue {

    public void executeNextRequest(PdvApiRequestCallback callback);
}
