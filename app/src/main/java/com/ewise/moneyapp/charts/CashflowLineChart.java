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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter.*;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by SilmiNawaz on 19/9/16.
 */
public class CashflowLineChart {

    private Context context;
    private List<TransactionCardDataObject> dataObject;
    private eGroupTransactionsBy groupTransactionsBy;
    int xAxisMaxDisplayValues;
    LineChart chart;

    public CashflowLineChart (Context context, LineChart chart, List<TransactionCardDataObject> dataObject, eGroupTransactionsBy groupTransactionsBy, int xAxisMaxDisplayValues){

        Log.d("CashflowLineChart", "Constructor - START");

        this.context = context;
        this.chart = chart;
        this.dataObject = dataObject;
        this.groupTransactionsBy = groupTransactionsBy;
        this.xAxisMaxDisplayValues = xAxisMaxDisplayValues;

        try {

            generateChart();
        }
        catch (Exception e){
            generalExceptionHandler(e.getClass().getName(), e.getMessage(), this.toString() + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ", this.toString());
        }

        Log.d("CashflowLineChart", "Constructor - END");

    }

    /**
     * call to get the generated cashflow line chart
     * @return
     * LineChart representing the data from the dataObject data fed into the CashflowLineChart constructor
     */
    public LineChart getChart()
    {
        return chart;
    }

    public void reDraw(){
        if (dataObject!=null){
            if (dataObject.size()>0){
                chart.invalidate();
            }
        }
    }

    /**
     * Call to animate the chart
     */
    public void animateChart (int XDurationMS, int YDurationMS){
        Log.d("CashflowLineChart", "animateChart() - START");

        chart.animateXY(XDurationMS,YDurationMS, Easing.EasingOption.EaseInElastic, Easing.EasingOption.EaseInCubic);

        Log.d("CashflowLineChart", "animateChart() - END");

    }


    /**
     * used internally to generate the chart
     */
    private void generateChart(){

        Log.d("CashflowLineChart", "generateChart() - START");


        setChartAttributes();

        //create the dataset list to be added to the chart data
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getLeftYAxisDataSet());

        //create the chartdata using the two data sets
        LineData lineData = new LineData(dataSets);

        //set the chart data to the chart
        chart.setData(lineData);

        configure_Legend();
        configure_left_yAxis();

        Log.d("CashflowLineChart", "generateChart() - END");
    }



    private void setChartAttributes(){

        Log.d("CashflowLineChart", "setChartAttributes() - START");


        //1. setup the chart attributes
        Description desc = new Description();
        desc.setText(context.getString(R.string.accountdetails_cashflow_linechart_description));
        desc.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_description_text_size)).floatValue());
        chart.setDescription(desc);

        chart.setNoDataText(context.getString(R.string.accountdetails_cashflow_linechart_nodatatext));
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.setMaxVisibleValueCount(12);
        chart.setHighlightPerDragEnabled(true);

        Log.d("CashflowLineChart", "setChartAttributes() - END");

    }


    private LineDataSet getLeftYAxisDataSet(){

        Log.d("CashflowLineChart", "getLeftYAxisDataSet() - START");

        //create the data entries for the chart - there will be two lines (dual y axis)
        //1st Y axis - cashflow
        List<Entry> entries = new ArrayList<>();
        //Log.d("**TRACE-Format**", String.format("cardlistsize = %d", dataObject.size()));
        String[] x_axis_text = new String[dataObject.size()];

        //get values from dataObject
        int x = 0;
        for (TransactionCardDataObject transaction: dataObject) {
            BigDecimal cashflowAmount = transaction.totalCashIn.add(transaction.totalCashOut);
            entries.add(new Entry(x, cashflowAmount.floatValue()));
            String x_axis_text_value = transaction.transactionMonth; //default is monthly
            switch (groupTransactionsBy){
                case DAY:
                    SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.api_transaction_date_format), Locale.getDefault());
                    x_axis_text_value = dateFormat.format(transaction.transactionDate);
                    break;
                case MONTH:
                    x_axis_text_value = transaction.transactionMonth;
                    break;
                case YEAR:
                    x_axis_text_value = transaction.transactionYear;

            }
            x_axis_text[x] = x_axis_text_value;
            //Log.d("**TRACE-Format** Cash", "getLeftYAxisDataSet() : " + String.format("x_axis_text[%d] = %s", x, x_axis_text_value));
            x++;
        }


        configure_xAxis(x_axis_text);


        LineDataSet dataSet = new LineDataSet(entries, context.getString(R.string.accountdetails_cashflow_linechart_cashflow_label));
        dataSet.setLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_line_width)).floatValue());
        dataSet.setColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        dataSet.setValueTextSize(Float.valueOf(context.getString(R.string.ewise_chart_text_size)).floatValue());
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawValues(true);
        dataSet.setDrawVerticalHighlightIndicator(true);

        Log.d("CashflowLineChart", "getLeftYAxisDataSet() - END");


        return dataSet;
    }


    private void configure_xAxis(String[] x_axis_text){

        Log.d("CashflowLineChart", "configure_xAxis() - START");


        XAxis x = chart.getXAxis();
        x.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseMainTextBlack));
        x.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_text_size)));
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setLabelRotationAngle(315f);
        x.setAxisLineColor(ContextCompat.getColor(context, R.color.coloreWiseMainTextBlack));
        x.setGranularity(1f);


        IAxisValueFormatter formatter = new CashflowXAxisValueFormatter(x_axis_text);

        x.setValueFormatter(formatter);

        Log.d("CashflowLineChart", "configure_xAxis() - END");

    }


    private void configure_left_yAxis(){
        Log.d("CashflowLineChart", "configure_left_yAxis() - START");

        //configure the left Y axis (cashflow)
        YAxis y_Left = chart.getAxisLeft();
        y_Left.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        y_Left.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_axis_text_size)));
        //y_Left.setAxisMaxValue(100f);//temp set y axis max - can reset later with real data
        y_Left.setDrawGridLines(true);
        y_Left.setGridColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        y_Left.setGridLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_gridline_width)));
        y_Left.setAxisLineColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        Log.d("CashflowLineChart", "configure_left_yAxis() - END");


    }


    //configure the chart legend
    private void configure_Legend() {
        Log.d("CashflowLineChart", "configure_Legend() - START");

        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        //legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseSecondaryTextBlack));
        Log.d("CashflowLineChart", "configure_Legend() - END");

    }



    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


    public class CashflowXAxisValueFormatter implements IAxisValueFormatter {


        private String[] mValues;

        public CashflowXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            if (((int) value < mValues.length) && ((int) value >=0)) {
                Log.d("CashflowXAxisValueFor..", String.format("getFormattedValue() : value: %d | stringValue= %s", (int) value, mValues[(int) value]));
                return mValues[(int) value];
            }

            return "";
        }

        /** this is only needed if numbers are returned, else return 0 */

        //todo: **mpandroidchart 3.0.2 broke this override** to be fixed/ replaced if needed
        //@Override
        public int getDecimalDigits() { return 0; }
    }
}
