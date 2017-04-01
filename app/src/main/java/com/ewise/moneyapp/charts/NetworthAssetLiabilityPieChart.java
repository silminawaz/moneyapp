package com.ewise.moneyapp.charts;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.NetworthDataObject;
import com.ewise.moneyapp.data.NetworthDataObject.*;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SilmiNawaz on 27/3/17.
 */
public class NetworthAssetLiabilityPieChart implements OnChartValueSelectedListener {

    Context context;
    List<NetworthEntry> dataObject;
    PieChart chart;

    public NetworthAssetLiabilityPieChart(PieChart chart, List<NetworthEntry> dataObject, Context context){
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
    public PieChart getChart()
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
    public class NetworthPercentFormatter implements IValueFormatter, IAxisValueFormatter {

        protected DecimalFormat mFormat;

        public NetworthPercentFormatter() {
            mFormat = new DecimalFormat("###,###,##0");
        }

        /**
         * Allow a custom decimalformat
         *
         * @param format
         */
        public NetworthPercentFormatter(DecimalFormat format) {
            this.mFormat = format;
        }

        // IValueFormatter
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if(Float.compare(value, 1f)<0){
                return "";
            }
            else {
                return mFormat.format(value) + " %";
            }
        }

        // IAxisValueFormatter
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mFormat.format(value) + " %";
        }

        public int getDecimalDigits() {
            return 1;
        }
    }


    private void generateChart(){

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

//        chart.setTransparentCircleColor(Color.WHITE);
//        chart.setTransparentCircleAlpha(110);

//        chart.setHoleRadius(58f);
//        chart.setTransparentCircleRadius(61f);

        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        chart.setDrawHoleEnabled(false);
        chart.setDrawCenterText(false);
        chart.setRotationEnabled(false);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        setData();

        chart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setDrawEntryLabels(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTypeface(Typeface.DEFAULT);
        chart.setEntryLabelTextSize(12f);

    }

    private void setData() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < dataObject.size() ; i++) {
            entries.add(new PieEntry(
                    dataObject.get(i).getAccountCard().preferredCurrencyBalance.floatValue(),
                    dataObject.get(i).getAccountCard().getTitle()
                    //ContextCompat.getDrawable(context, R.drawable.star)
            ));
        }

        PieDataSet dataSet = new PieDataSet(entries, ""); //todo:replace

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        //ArrayList<Integer> colors = new ArrayList<Integer>();
        List<Integer> colors = null;
        if (dataObject.size()>0) {
            colors = getColors(dataObject.get(0).getType());
        }
        else
        {
            colors = getColors(eNetworthType.ASSET);
        }

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new NetworthPercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.coloreWiseSecondaryTextBlack));
        data.setValueTypeface(Typeface.DEFAULT);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    private List<Integer> getColors(eNetworthType networthType) {

        int[] colorArray;

        if (networthType.equals(eNetworthType.ASSET)){
            colorArray=context.getResources().getIntArray(R.array.coloreWiseAssetChartList);
        }
        else{
            colorArray=context.getResources().getIntArray(R.array.coloreWiseLiabilityChartList);
        }


        return ColorTemplate.createColors(colorArray);

    }


    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }


    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
