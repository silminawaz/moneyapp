package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.Utils.PdvApiResults;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 22/3/17.
 */
public class NetworthDataObject {


    public enum eNetworthType{
        ASSET,
        LIABILITY,
        UNKNOWN;
    }

    public class NetworthEntry {
        eNetworthType type;
        AccountCardDataObject accountCard;

        public eNetworthType getType(){
            return type;
        }

        public AccountCardDataObject getAccountCard(){
            return accountCard;
        }
    }



    private Context context;
    Map<AccountCardDataObject.eAccountCategory, eNetworthType> typeToCategoryMap;
    Map<AccountCardDataObject.eAccountCategory, eNetworthType> typeToNegativeCategoryMap;

    private BigDecimal totalAssetsAmount=null;
    private BigDecimal totalLiabilitiesAmount=null;
    private BigDecimal totalUnknownAmount=null;
    private Currency currency;

    List<NetworthEntry> assetsList;
    List<NetworthEntry> liabilitiesList;
    List<NetworthEntry> unknownList;

    public NetworthDataObject(Context context, AccountCardListDataObject accountCardList, String baseCurrency){
        this.context = context;
        typeToCategoryMap = new HashMap<AccountCardDataObject.eAccountCategory, eNetworthType>();
        typeToNegativeCategoryMap = new HashMap<AccountCardDataObject.eAccountCategory, eNetworthType>();
        currency = Currency.getInstance(baseCurrency);
        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        double dZero = 0.0;
        //this.totalAssetsAmount = new BigDecimal(dZero, mc);

        //this.totalLiabilitiesAmount = new BigDecimal(dZero, mc);
        //this.totalUnknownAmount = new BigDecimal(dZero, mc);

        assetsList = new ArrayList<>();
        liabilitiesList = new ArrayList<>();
        unknownList = new ArrayList<>();

        //Create the tile category to networth type map
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.CASH, eNetworthType.ASSET);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.INVESTMENT, eNetworthType.ASSET);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.RETIREMENT, eNetworthType.ASSET);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.PROPERTY, eNetworthType.ASSET);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.OTHERASSETS, eNetworthType.ASSET);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.CREDIT, eNetworthType.LIABILITY);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.LOAN, eNetworthType.LIABILITY);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.BILLS, eNetworthType.LIABILITY);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.OTHERLIABILITIES, eNetworthType.LIABILITY);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.INSURANCE, eNetworthType.LIABILITY);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.REWARDS, eNetworthType.UNKNOWN);
        typeToCategoryMap.put(AccountCardDataObject.eAccountCategory.UNKNOWN, eNetworthType.UNKNOWN);

        //exception to the rule, when assets become a liability (OVERDRAWN ASSET TYPE e.g Overdrawn CASH account) and a liability becomes an asset (OVERPAID liability type e.g. Credit card)
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.CASH, eNetworthType.LIABILITY);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.INVESTMENT, eNetworthType.LIABILITY);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.RETIREMENT, eNetworthType.LIABILITY);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.PROPERTY, eNetworthType.LIABILITY);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.OTHERASSETS, eNetworthType.LIABILITY);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.CREDIT, eNetworthType.ASSET);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.LOAN, eNetworthType.ASSET);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.BILLS, eNetworthType.ASSET);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.OTHERLIABILITIES, eNetworthType.ASSET);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.INSURANCE, eNetworthType.ASSET);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.REWARDS, eNetworthType.UNKNOWN);
        typeToNegativeCategoryMap.put(AccountCardDataObject.eAccountCategory.UNKNOWN, eNetworthType.UNKNOWN);

        calculateNetworth(accountCardList);
    }

    private void calculateNetworth (AccountCardListDataObject accountCardList){

        Log.d ("NetworthDataObject", "calculateNetworth() - START");

            if (accountCardList != null) {
                try {
                    if (accountCardList.getAccountCardList() != null) {
                        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
                        CurrencyExchangeRates rates = CurrencyExchangeRates.getInstance();
                        List<AccountCardDataObject> acoList = accountCardList.getAccountCardList();
                        for (AccountCardDataObject aco : acoList) {
                            NetworthEntry e = new NetworthEntry();
                            e.accountCard = aco;
                            e.type = (aco.preferredCurrencyBalance.doubleValue()>=0)?typeToCategoryMap.get(aco.category):typeToNegativeCategoryMap.get(aco.category);
                            switch (e.type) {
                                case ASSET:
                                    if (totalAssetsAmount==null){
                                        totalAssetsAmount = new BigDecimal(Math.abs(aco.preferredCurrencyBalance.doubleValue()));
                                    }
                                    else
                                    {
                                        totalAssetsAmount = BigDecimal.valueOf(Math.abs(aco.preferredCurrencyBalance.doubleValue())).add(BigDecimal.valueOf(totalAssetsAmount.doubleValue()));

                                    }
                                    this.assetsList.add(e);
                                    break;
                                case LIABILITY:
                                    if (totalLiabilitiesAmount==null){
                                        totalLiabilitiesAmount = new BigDecimal(Math.abs(aco.preferredCurrencyBalance.doubleValue()));
                                    }
                                    else
                                    {
                                        totalLiabilitiesAmount = BigDecimal.valueOf(Math.abs(aco.preferredCurrencyBalance.doubleValue())).add(BigDecimal.valueOf(totalLiabilitiesAmount.doubleValue()));

                                    }
                                    this.liabilitiesList.add(e);
                                    break;
                                default:
                                    if (totalUnknownAmount==null){
                                        totalUnknownAmount = new BigDecimal( aco.preferredCurrencyBalance.doubleValue());
                                    }
                                    else
                                    {
                                        totalUnknownAmount = BigDecimal.valueOf(aco.preferredCurrencyBalance.doubleValue()).add(BigDecimal.valueOf(totalUnknownAmount.doubleValue()));

                                    }
                                    this.unknownList.add(e);
                                    break;
                            }
                        }
                    }
                }
                catch (Exception e){
                    String sObjString = accountCardList.toString();
                    generalExceptionHandler(
                            e.getClass().getName(),
                            this.toString()+Thread.currentThread().getStackTrace()[2].getClassName()+"()",
                            e.getMessage(),
                            sObjString);
                }
            }

        Log.d ("NetworthDataObject", "calculateNetworth() - END");

    }

    public BigDecimal getTotalAssetsAmount() {
        Log.d ("NetworthDataObject", "getTotalAssetsAmount() =" + totalAssetsAmount);
        if (totalAssetsAmount!=null) {
            return totalAssetsAmount;
        }
        else
        {
            return new BigDecimal(0);
        }
    }

    public BigDecimal getTotalLiabilitiesAmount() {
        Log.d ("NetworthDataObject", "getTotalLiabilitiesAmount() =" + totalLiabilitiesAmount);

        if(totalLiabilitiesAmount!=null) {
            return totalLiabilitiesAmount;
        }
        else
        {
            return new BigDecimal(0);
        }
    }

    public BigDecimal getTotalUnknownAmount() {
        Log.d ("NetworthDataObject", "getTotalUnknownAmount() =" + totalUnknownAmount);
        if(totalUnknownAmount!=null) {
            return totalUnknownAmount;
        }
        else
        {
            return new BigDecimal(0);
        }
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<NetworthEntry> getAssetsList() {
        return assetsList;
    }

    public List<NetworthEntry> getLiabilitiesList() {
        return liabilitiesList;
    }

    public List<NetworthEntry> getUnknownList() {
        return unknownList;
    }



    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = this.context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
