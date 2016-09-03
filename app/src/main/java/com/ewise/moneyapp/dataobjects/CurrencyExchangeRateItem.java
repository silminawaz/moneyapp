package com.ewise.moneyapp.dataobjects;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Created by SilmiNawaz on 28/8/16.
 */
public class CurrencyExchangeRateItem {
    public String baseCurrencyCode; //e.g. SGD       USD           USD         SGD
    public String quoteCurrencyCode;//e.g. USD       SGD           SGD         USD
    public BigDecimal exchangeRate; //e.g. 0.7357    1.3592        0.7357      1.3592
    public boolean rateQuoteDirect; //e.g. true      true          false       false

    public CurrencyExchangeRateItem(String baseCurrencyCode,
                                    String quoteCurrencyCode,
                                    boolean rateQuoteDirect,
                                    BigDecimal exchangeRate){

        this.baseCurrencyCode = baseCurrencyCode;
        this.quoteCurrencyCode = quoteCurrencyCode;
        this.exchangeRate = exchangeRate;
        this.rateQuoteDirect = rateQuoteDirect;
    }

    public boolean RateIsValidForCurrency (String validCurrencyCode){

        if (validCurrencyCode.equals(this.baseCurrencyCode)){
            return true;
        }
        else {
            if (validCurrencyCode.equals(this.quoteCurrencyCode)) {
                return true;
            }
        }

        return false;
    }

    public BigDecimal ExchangeToQuoteCurrency (BigDecimal baseCurrencyAmount){

        BigDecimal quoteCurrencyAmount;
        Currency baseCurrency = Currency.getInstance(this.baseCurrencyCode);
        Currency quoteCurrency = Currency.getInstance(this.quoteCurrencyCode);
        int precision = 10;
        precision = precision + quoteCurrency.getDefaultFractionDigits(); //make sure we support the fractions


        if (rateQuoteDirect){
            //determine the precision including fractions
            int scale = baseCurrency.getDefaultFractionDigits() + exchangeRate.scale();
            precision = precision + scale;

            quoteCurrencyAmount = baseCurrencyAmount.multiply(exchangeRate, new MathContext(precision, RoundingMode.HALF_UP));
            quoteCurrencyAmount = quoteCurrencyAmount.setScale(quoteCurrency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }
        else {
            quoteCurrencyAmount = baseCurrencyAmount.divide(exchangeRate,quoteCurrency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }

        return quoteCurrencyAmount;

    }

    public BigDecimal ExchangeToBaseCurrency (BigDecimal quoteCurrencyAmount){
        BigDecimal baseCurrencyAmount;
        Currency baseCurrency = Currency.getInstance(this.baseCurrencyCode);
        Currency quoteCurrency = Currency.getInstance(this.quoteCurrencyCode);
        int precision = 10;
        precision = precision + baseCurrency.getDefaultFractionDigits(); //make sure we support the fractions

        if (rateQuoteDirect){
            baseCurrencyAmount = quoteCurrencyAmount.divide(exchangeRate,baseCurrency.getDefaultFractionDigits(), RoundingMode.HALF_UP);

        }
        else {
            int scale = baseCurrency.getDefaultFractionDigits() + exchangeRate.scale();
            precision = precision + scale;

            baseCurrencyAmount = quoteCurrencyAmount.multiply(exchangeRate, new MathContext(precision, RoundingMode.HALF_UP));
            baseCurrencyAmount = baseCurrencyAmount.setScale(baseCurrency.getDefaultFractionDigits(), RoundingMode.HALF_UP);

        }

        return baseCurrencyAmount;
    }
}
