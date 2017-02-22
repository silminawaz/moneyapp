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
import com.github.mikephil.charting.formatter.AxisValueFormatter.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by SilmiNawaz on 19/9/16.
 */
public class CashflowLineChart {

    private Context context;
    private List<TransactionCardDataObject> transactioncardList;
    private eGroupTransactionsBy groupTransactionsBy;
    int xAxisMaxDisplayValues;
    LineChart chart;

    public CashflowLineChart (Context context, LineChart chart, List<TransactionCardDataObject> transactionCardList, eGroupTransactionsBy groupTransactionsBy, int xAxisMaxDisplayValues){

        this.context = context;
        this.chart = chart;
        this.transactioncardList = transactionCardList;
        this.groupTransactionsBy = groupTransactionsBy;
        this.xAxisMaxDisplayValues = xAxisMaxDisplayValues;

        generateChart();
    }

    /**
     * call to get the generated cashflow line chart
     * @return
     * LineChart representing the data from the TransactionCardList data fed into the CashflowLineChart constructor
     */
    public LineChart getChart()
    {
        return chart;
    }

    public void reDraw(){
        chart.invalidate();
    }

    /**
     * Call to animate the chart
     */
    public void animateChart (int XDurationMS, int YDurationMS){
        chart.animateXY(XDurationMS,YDurationMS, Easing.EasingOption.EaseInElastic, Easing.EasingOption.EaseInCubic);
    }


    /**
     * used internally to generate the chart
     */
    private void generateChart(){

        setChartAttributes();

        //create the dataset list to be added to the chart data
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(getLeftYAxisDataSet());
        //dataSets.add(getLeftYAxisDataSet());
        //dataSets.add(getRightYAxisDataSet());

        //create the chartdata using the two data sets
        LineData lineData = new LineData(dataSets);

        //set the chart data to the chart
        chart.setData(lineData);

        configure_Legend();
        //configure_xAxis();
        configure_left_yAxis();
        //configure_right_yAxis();
    }



    private void setChartAttributes(){

        //1. setup the chart attributes
        chart.setDescription(context.getString(R.string.accountdetails_cashflow_linechart_description));
        chart.setDescriptionTextSize(Float.valueOf(context.getString(R.string.ewise_chart_description_text_size)).floatValue());
        chart.setNoDataText(context.getString(R.string.accountdetails_cashflow_linechart_nodatatext));
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        //chart.setMaxVisibleValueCount(12);
        chart.setHighlightPerDragEnabled(true);
    }


    private LineDataSet getLeftYAxisDataSet(){
        //create the data entries for the chart - there will be two lines (dual y axis)
        //1st Y axis - cashflow
        List<Entry> entries = new ArrayList<>();
        Log.d("**TRACE-Format**", String.format("cardlistsize = %d", transactioncardList.size()));
        String[] x_axis_text = new String[transactioncardList.size()];

        //get values from transactionCardList
        int x = 0;
        for (TransactionCardDataObject transaction: transactioncardList) {
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
            Log.d("**TRACE-Format**", String.format("x_axis_text[%d] = %s", x, x_axis_text_value));
            x++;
        }

        configure_xAxis(x_axis_text);

        /*
        entries.add(new Entry(0, 10945.50f));
        entries.add(new Entry(1, 34444.5f));
        entries.add(new Entry(2, 33333.50f));
        entries.add(new Entry(3, 41111f));
        entries.add(new Entry(4, 55555f));
        entries.add(new Entry(5, 101101f));
        */

        //create the datasets using the data entries
        //1. cashflow data set
        LineDataSet dataSet = new LineDataSet(entries, context.getString(R.string.accountdetails_cashflow_linechart_cashflow_label));
        dataSet.setLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_line_width)).floatValue());
        dataSet.setColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        dataSet.setValueTextSize(Float.valueOf(context.getString(R.string.ewise_chart_text_size)).floatValue());
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(true);

        return dataSet;
    }

    private LineDataSet getRightYAxisDataSet(){
        //2nd y axis - account balance
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 1105555));
        entries.add(new Entry(1, 423445));
        entries.add(new Entry(2, -333445));
        entries.add(new Entry(3, -444445));
        entries.add(new Entry(4, 666995));
        entries.add(new Entry(5, 999995));

        LineDataSet dataSet = new LineDataSet(entries, context.getString(R.string.accountdetails_cashflow_linechart_balance_label));
        dataSet.setLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_line_width)).floatValue());
        dataSet.setColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartBalanceLine));
        dataSet.setValueTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartBalanceLine));
        dataSet.setValueTextSize(Float.valueOf(context.getString(R.string.ewise_chart_text_size)).floatValue());
        dataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSet.setDrawValues(false);
        dataSet.setDrawVerticalHighlightIndicator(true);

        return dataSet;
    }

    private void configure_xAxis(String[] x_axis_text){

        XAxis x = chart.getXAxis();
        x.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseMainTextBlack));
        x.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_text_size)));
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(false);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setLabelRotationAngle(315f);
        x.setAxisLineColor(ContextCompat.getColor(context, R.color.coloreWiseMainTextBlack));
        x.setGranularity(1f);

        /*
        //we have to draw the relevant data string in the x-axis representing each float value in x-axis
        final String[] x_axis_text = new String[] { "Jan-16",
                                                    "Feb-16",
                                                    "Mar-16",
                                                    "Apr-16",
                                                    "May-16",
                                                    "Jun-16"};
        */

//        final String[] x_axis_text_inner = x_axis_text;

        AxisValueFormatter formatter = new CashflowXAxisValueFormatter(x_axis_text);


/*
        AxisValueFormatter formatter = new AxisValueFormatter() {


            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return x_axis_text_inner[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };
*/
        x.setValueFormatter(formatter);
    }


    private void configure_left_yAxis(){
        //configure the left Y axis (cashflow)
        YAxis y_Left = chart.getAxisLeft();
        y_Left.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        y_Left.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_axis_text_size)));
        //y_Left.setAxisMaxValue(100f);//temp set y axis max - can reset later with real data
        y_Left.setDrawGridLines(true);
        y_Left.setGridColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
        y_Left.setGridLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_gridline_width)));
        y_Left.setAxisLineColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartCashflowLine));
    }


    private void configure_right_yAxis(){
        //configure the right Y axis (balance)
        YAxis y_Right = chart.getAxisRight();
        y_Right.setEnabled(true);//change later
        y_Right.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartBalanceLine));
        y_Right.setTextSize(Float.valueOf(context.getString(R.string.ewise_chart_axis_text_size)));
        //y_Right.setAxisMaxValue(1100f);//temp set y axis max - can reset later with real data
        y_Right.setDrawGridLines(true);
        y_Right.setGridColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartBalanceLine));
        y_Right.setGridLineWidth(Float.valueOf(context.getString(R.string.ewise_chart_gridline_width)));
        y_Right.setAxisLineColor(ContextCompat.getColor(context, R.color.coloreWiseLineChartBalanceLine));
    }

    //configure the chart legend
    private void configure_Legend() {
        Legend legend = chart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        legend.setTextColor(ContextCompat.getColor(context, R.color.coloreWiseSecondaryTextBlack));
    }



    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }


    public class CashflowXAxisValueFormatter implements AxisValueFormatter {

        private String[] mValues;

        public CashflowXAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            Log.d("**TRACE-Format**", String.format("value: %d", (int) value));
            if (((int) value < mValues.length) && ((int) value >=0)) {
                return mValues[(int) value];
            }

            return "";

        }

        /** this is only needed if numbers are returned, else return 0 */
        @Override
        public int getDecimalDigits() { return 0; }
    }
}
