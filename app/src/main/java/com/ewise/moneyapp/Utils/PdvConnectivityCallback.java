package com.ewise.moneyapp.Utils;

import android.content.Context;
import android.widget.Toast;

import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Providers;

import java.util.List;

/**
 * Created by SilmiNawaz on 22/2/17.
 */

public interface PdvConnectivityCallback {
    public void onPdvConnected();
    public void onPdvDisconnected();
    public void onGetInstitutionsFail(PdvApiResults results);
    public void onGetInstitutionsSuccess(PdvApiResults results);
    public void onGetPromptsSuccess(PdvApiResults results);
    public void onGetPromptsFail(PdvApiResults results);
    public void onGetUserProfileSuccess(PdvApiResults results);
    public void onGetUserProfileFail(PdvApiResults results);
    public void onRestoreAccountsComplete(String instId);
    public void onRestoreAccountsAllComplete();
    public void onRestoreTransactionsAllComplete(PdvApiResults results);
    public void onRestoreTransactionsFail(PdvApiResults results);
    public void onGetCredentialSuccess(PdvApiResults results);
    public void onGetCredentialFail(PdvApiResults results);
    public void onSetCredentialSuccess(PdvApiResults results);
    public void onSetCredentialFail(PdvApiResults results);



    /*
    copy-paste to implementation class

    //Begin: PdvConnectivityCallback implementation
    @Override
    public void onPdvConnected()
    {

    }

    @Override
    public void onPdvDisconnected()
    {

    }

    @Override
    public void onGetInstitutionsFail(PdvApiResults results)
    {

    }

    @Override
    public void onGetInstitutionsSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetPromptsSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetPromptsFail(PdvApiResults results)
    {

    }

    @Override
    public void onGetUserProfileSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetUserProfileFail(PdvApiResults results)
    {

    }

    @Override
    public void onRestoreAccountsComplete(String instId)
    {

    }

    @Override
    public void onRestoreAccountsAllComplete()
    {

    }

    @Override
    public void onRestoreTransactionsAllComplete(PdvApiResults results)
    {

    }

    @Override
    public void onRestoreTransactionsFail(PdvApiResults results)
    {

    }

    @Override
    public void onGetCredentialSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onGetCredentialFail(PdvApiResults results)
    {

    }

    @Override
    public void onSetCredentialSuccess(PdvApiResults results)
    {

    }

    @Override
    public void onSetCredentialFail(PdvApiResults results)
    {

    }
    //End: PdvConnectivityCallback implementation


     */
}
