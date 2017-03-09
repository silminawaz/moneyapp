package com.ewise.moneyapp.Utils;

import com.ewise.android.pdv.api.model.PromptEntry;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by SilmiNawaz on 5/3/17.
 */
public class PdvApiRequestParams {

    public PdvApiRequestParams(){
        this.uuid = UUID.randomUUID().toString();
        logonParams = new LogonParams();
        updateParams = new UpdateParams();
    }

    public class LogonParams{
        public String userName;
        public String password;
    }

    public class UpdateParams{
        public List<String>        instIds;
        public String              profileId;
        public List<PromptEntry>   credPrompts;
        public Date                transactionStartDate;
        public Date                transactionEndDate;
    }

    private String uuid;
    public PdvApiName pdvApiName;
    public PdvApiStatus pdvApiStatus;
    public LogonParams logonParams;
    public UpdateParams updateParams;
    public PdvApiResults results;

    public String getUuid() {
        return uuid;
    }

}
