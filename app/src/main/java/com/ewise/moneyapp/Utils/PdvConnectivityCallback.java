package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Providers;

/**
 * Created by SilmiNawaz on 22/2/17.
 */

public interface PdvConnectivityCallback {
    public void onPdvConnected();
    public void onPdvDisconnected();
    public void onGetInstitutionsFail(PdvApiResults results);
    public void onGetInstitutionsSuccess(PdvApiResults results);

}
