package com.ewise.moneyapp.data;

import android.content.Context;
import android.util.Log;

import com.ewise.moneyapp.R;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SilmiNawaz on 22/3/17.
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
    }



    private Context context;
    Map<AccountCardDataObject.eAccountCategory, eNetworthType> typeToCategoryMap;

    BigDecimal totalAssetsAmount;
    BigDecimal totalLiabilitiesAmount;
    BigDecimal totalUnknownAmount;
    Currency currency;

    List<NetworthEntry> assetsList;
    List<NetworthEntry> liabilitiesList;
    List<NetworthEntry> unknownList;

    public NetworthDataObject(Context context, AccountCardListDataObject accountCardList, String baseCurrency){
        this.context = context;
        typeToCategoryMap = new HashMap<AccountCardDataObject.eAccountCategory, eNetworthType>();
        currency = Currency.getInstance(baseCurrency);
        MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        double dZero = 0.0;
        this.totalAssetsAmount = new BigDecimal(dZero, mc);
        this.totalLiabilitiesAmount = new BigDecimal(dZero, mc);
        this.totalUnknownAmount = new BigDecimal(dZero, mc);

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

        calculateNetworth(accountCardList);
    }

    private void calculateNetworth (AccountCardListDataObject accountCardList){
        if (accountCardList!=null){
            if (accountCardList.getAccountCardList()!=null){
                MathContext mc = new MathContext(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
                CurrencyExchangeRates rates = CurrencyExchangeRates.getInstance();
                List<AccountCardDataObject> acoList = accountCardList.getAccountCardList();
                for (AccountCardDataObject aco : acoList){
                    switch (typeToCategoryMap.get(aco.category)){
                        case ASSET:
                            this.totalAssetsAmount.add(aco.preferredCurrencyBalance, mc);
                            break;
                        case LIABILITY:
                            this.totalLiabilitiesAmount.add(aco.preferredCurrencyBalance, mc);
                            break;
                        default:
                            this.totalUnknownAmount.add(aco.preferredCurrencyBalance, mc);
                            break;
                    }
                }
            }
        }
    }
    


    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = this.context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
