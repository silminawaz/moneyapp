package com.ewise.moneyapp.Utils;

import com.ewise.android.pdv.api.PdvApi;
import com.ewise.android.pdv.api.model.Response;
import com.ewise.android.pdv.api.model.provider.Providers;
import com.ewise.android.pdv.api.model.response.AccountsResponse;
import com.ewise.android.pdv.api.model.response.GetPromptsResponse;
import com.ewise.android.pdv.api.model.response.GetUserProfileResponse;
import com.ewise.android.pdv.api.model.response.TransactionsResponse;

/**
 * Created by SilmiNawaz on 21/2/17.
 * common result data model for any PdvApi call using
 */
public class PdvApiResults {

    public int      timeoutInterval = 0;
    public boolean  callBackCompleted = false;
    public boolean  timeout = false;

    //response for setUser() API
    public Response                 setUserResponse = null;

    //response status for initialise() API
    public String                   initialiseStatus = "";

    //response for getInstitutions() API
    public Response<Providers>      providers = null;

    //response for getUserProfile() API
    public GetUserProfileResponse   userProfile = null;

    //response for getPrompts() API
    public GetPromptsResponse       prompts = null;

    //response for updateAccounts(), restoreAccounts() APIs
    public AccountsResponse         accounts = null;

    //response for updateTransactions(), restoreTransactions() APIs
    public TransactionsResponse     transactions = null;

}