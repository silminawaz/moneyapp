package com.ewise.moneyapp.charts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.NetworthDataObject;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by SilmiNawaz on 26/3/17.
 */
public class NetworthHBarChart {

    Context context;
    HorizontalBarChart chart;
    NetworthDataObject dataObject;

    public NetworthHBarChart(HorizontalBarChart chart, NetworthDataObject dataObject, Context context){
        this.context = context;
        this.chart = chart;
        this.dataObject = dataObject;

        generateChart();
    }

    /**
     * call to get the generated cashflow line chart
     * @return
     * LineChart representing the data from the TransactionCardList data fed into the CashflowLineChart constructor
     */
    public HorizontalBarChart getChart()
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


    //use this class to hide the drawing of "0" values directly above the Axis
    public class NetworthValueFormatter implements IValueFormatter {

        DecimalFormat decimalFormat;

        public NetworthValueFormatter(DecimalFormat decimalFormat){
            this.decimalFormat = decimalFormat;
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            // write your logic here
            if (Float.compare(value, 0f)==0){
                return "";
            }

            return decimalFormat.format(value); // e.g. append a dollar-sign
        }


        /** this is only needed if numbers are returned, else return 0 */

        //todo: **mpandroidchart 3.0.2 broke this override** to be fixed/ replaced if needed
        //@Override
        public int getDecimalDigits() { return 0; }
    }

    private void generateChart(){
        // chart.setOnChartValueSelectedListener(this);
        // chart.setHighlightEnabled(false);

        chart.setDrawBarShadow(false);

        chart.setDrawValueAboveBar(true);

        chart.setMinimumHeight(50);

        //chart.getDescription().setEnabled(false);

        Description desc = new Description();
        desc.setText("");
        chart.setDescription(desc);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setDrawTopYLabelEntry(false);
        chart.getXAxis().setDrawLabels(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);

        // draw shadows for each bar that show the maximum value
        // chart.setDrawBarShadow(true);

        chart.setDrawGridBackground(false);

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(Typeface.DEFAULT);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        YAxis yl = chart.getAxisLeft();
        yl.setTypeface(Typeface.DEFAULT);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = chart.getAxisRight();
        yr.setTypeface(Typeface.DEFAULT);
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setAxisMaximum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        setData();
        chart.setFitBars(true);
        //chart.animateY(2500);

        // setting data
        //mSeekBarY.setProgress(50);
        //mSeekBarX.setProgress(12);

        //mSeekBarY.setOnSeekBarChangeListener(this);
        //mSeekBarX.setOnSeekBarChangeListener(this);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

    }


    private void setData() {

        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> yValsAssets = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValsLiabilities = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yValsUnknown = new ArrayList<BarEntry>();


        String labels[] = new String[2];
        labels[0] = context.getString(R.string.pdv_account_category_ASSETS);
        labels[1] = context.getString(R.string.pdv_account_category_LIABILITIES);

        yValsLiabilities.add(new BarEntry(0 * spaceForBar, dataObject.getTotalLiabilitiesAmount().floatValue(), context.getString(R.string.pdv_account_category_LIABILITIES)));
        yValsLiabilities.add(new BarEntry(1 * spaceForBar, 0f, context.getString(R.string.pdv_account_category_LIABILITIES)));

        yValsAssets.add(new BarEntry(0 * spaceForBar, 0f, context.getString(R.string.pdv_account_category_ASSETS)));
        yValsAssets.add(new BarEntry(1 * spaceForBar, dataObject.getTotalAssetsAmount().floatValue(), context.getString(R.string.pdv_account_category_ASSETS)));


        BarDataSet setAssets;
        BarDataSet setLiabilities;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            setAssets = (BarDataSet)chart.getData().getDataSetByIndex(0);
            setAssets.setValues(yValsAssets);
            setAssets.setColors(new int[]{R.color.coloreWisePrimary}, context);

            setLiabilities = (BarDataSet)chart.getData().getDataSetByIndex(1);
            setLiabilities.setValues(yValsLiabilities);
            setLiabilities.setColors(new int[]{R.color.coloreWiseHighlight}, context);

            chart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(labels));
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            setAssets = new BarDataSet(yValsAssets, context.getString(R.string.pdv_account_category_ASSETS));
            setLiabilities = new BarDataSet(yValsLiabilities, context.getString(R.string.pdv_account_category_LIABILITIES));

            //setAssets.setDrawIcons(false);
            setAssets.setLabel(context.getString(R.string.pdv_account_category_ASSETS));
            setLiabilities.setLabel(context.getString(R.string.pdv_account_category_LIABILITIES));


            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            setAssets.setColors(new int[]{R.color.coloreWisePrimary}, context);
            setLiabilities.setColors(new int[]{R.color.coloreWiseHighlight}, context);

            DecimalFormat df = new DecimalFormat();
            df.setMinimumFractionDigits(0);
            df.setMaximumFractionDigits(0);
            NetworthValueFormatter formatter = new NetworthValueFormatter(df);
            setAssets.setValueFormatter(formatter);
            setLiabilities.setValueFormatter(formatter);

            dataSets.add(setAssets);
            dataSets.add(setLiabilities);

            ArrayList<String> xVals = new ArrayList<String>();

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(Typeface.DEFAULT);
            data.setBarWidth(barWidth);
            chart.setData(data);

            chart.getAxisRight().setValueFormatter(new IndexAxisValueFormatter(labels));
        }
    }
    
    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
