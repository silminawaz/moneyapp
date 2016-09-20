package com.ewise.moneyapp.charts;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.TransactionCardListDataObject;
import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.data.TransactionCardDataObject.eGroupTransactionsBy;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;



/**
 * Created by SilmiNawaz on 19/9/16.
 */
public class CashflowLineChart {

    private Context context;
    private TransactionCardListDataObject transactioncardList;
    private eGroupTransactionsBy groupTransactionsBy;
    int xAxisMaxDisplayValues;

    public CashflowLineChart (Context context, TransactionCardListDataObject transactionCardList, eGroupTransactionsBy groupTransactionsBy, int xAxisMaxDisplayValues){

        this.context = context;
        this.transactioncardList = transactionCardList;
        this.groupTransactionsBy = groupTransactionsBy;
        this.xAxisMaxDisplayValues = xAxisMaxDisplayValues;

    }











    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
