package com.ewise.moneyapp.dataobjects;

import com.ewise.moneyapp.dataobjects.AccountObject;

import java.math.BigDecimal;

/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class CashAccountObject extends AccountObject {

    public CashAccountObject (String id,
                              String instId,
                              String instDesc,
                              String type,
                              String number,
                              String name,
                              String currency,
                              String balance,
                              String funds,
                              String data){

        super ( id,instId,instDesc,type,number,name,currency,balance,funds,data);
    }

    public BigDecimal getAvailableBalance()
    {
        return this.getBalanceAmount();
    }

    public BigDecimal getLedgerBalance()
    {
        return this.getFundsAmount();
    }


}
