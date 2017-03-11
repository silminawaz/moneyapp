package com.ewise.moneyapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class PdvAcaUpdateAccountsService extends Service {

    private final IBinder pdvApiRequestBinder = new PdvApiRequestBinder();

    public class PdvApiRequestBinder extends Binder{
        PdvAcaUpdateAccountsService getService(){
            return PdvAcaUpdateAccountsService.this;
        }
    }

    public PdvAcaUpdateAccountsService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return pdvApiRequestBinder;
    }





}
