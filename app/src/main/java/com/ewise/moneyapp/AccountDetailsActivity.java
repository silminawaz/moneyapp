package com.ewise.moneyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ewise.moneyapp.adapters.TransactionGroupByFilterAdapter;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.data.TransactionCardDataObject.eGroupTransactionsBy;
import com.ewise.moneyapp.data.TransactionCardListDataObject;
import com.ewise.moneyapp.data.TransactionGroupByFilterDataObject;
import com.ewise.moneyapp.loaders.PdvAccountTransactionResponseLoader;
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

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

@EActivity(R.layout.activity_account_details)
public class AccountDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<PdvTransactionResponse>, GestureDetector.OnGestureListener {

    @ViewById(R.id.accountdetails_filter_transactions_img)
    ImageView accountdetails_filter_transactions_img;

    @ViewById(R.id.accountdetails_cardview)
    CardView accountdetails_cardview;

    @ViewById(R.id.accountdetails_accountname)
    TextView accountdetails_accountname;

    @ViewById(R.id.accountdetails_accountnumber)
    TextView accountdetails_accountnumber;

    @ViewById(R.id.accountdetails_accountcurrency)
    TextView accountdetails_accountcurrency;

    @ViewById(R.id.accountdetails_accountbalance)
    TextView accountdetails_accountbalance;

    @ViewById(R.id.accountdetails_accounticon)
    ImageView accountdetails_accounticon;


    @ViewById(R.id.accountdetails_lastupdated)
    TextView accountdetails_lastupdated;

    @ViewById(R.id.accountdetails_transaction_select_spinner)
    Spinner accountdetails_transaction_select_spinner;

    @ViewById(R.id.accountdetails_transactions_groupby_spinner)
    Spinner accountdetails_transactions_groupby_spinner;

    PdvAccountResponse.AccountsObject _account = null;

    @ViewById(R.id.accountdetails_transactioncard_recycler_view)
    RecyclerView accountdetails_transactioncard_recycler_view;

    @ViewById(R.id.accountdetails_open_transactions_img)
    ImageView accountdetails_open_transactions_img;

    @ViewById(R.id.accountdetails_cashflowchart)
    ImageView accountdetails_cashflowchart;

    LineChart accountdetails_cashflowchart_linechart;

    TransactionCardsViewAdapter transactionCardsViewAdapter;
    TransactionGroupByFilterAdapter transactionGroupByFilterAdapter;

    private GestureDetectorCompat mDetector;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private int mSlop;
    private float mDownX;
    private float mDownY;
    private boolean mSwiping;

    public eGroupTransactionsBy groupTransactionsBy = eGroupTransactionsBy.DAY; //default group by


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(R.layout.activity_account_details);



    }

    @Override
    protected void onStart() {
        super.onStart();

        mDetector = new GestureDetectorCompat(this,this);


        Intent intent = getIntent();
        String accountObjJson = intent.getStringExtra("com.wise.moneyapp.data.PdvAccountResponse.AccountsObject");
        _account = PdvAccountResponse.AccountsObject.objectFromData(accountObjJson);

        accountdetails_accountname.setText(_account.accountName);
        accountdetails_accountnumber.setText(_account.accountNumber);
        accountdetails_accountbalance.setText(_account.balance);
        accountdetails_accountcurrency.setText(_account.currency);
        accountdetails_lastupdated.setText(_account.updatedAt);



        transactionGroupByFilterAdapter = new TransactionGroupByFilterAdapter(this,
                R.layout.transactions_groupby_spinner_item, getGroupTransactionsByFilter());
        accountdetails_transactions_groupby_spinner.setAdapter(transactionGroupByFilterAdapter);


        //Attach adapter and load the data
        accountdetails_transactioncard_recycler_view.setLayoutManager(new LinearLayoutManager(AccountDetailsActivity.this));
        transactionCardsViewAdapter = new TransactionCardsViewAdapter(AccountDetailsActivity.this);
        accountdetails_transactioncard_recycler_view.setAdapter(transactionCardsViewAdapter);
        getSupportLoaderManager().initLoader(R.id.PdvTransactionResponse_Loader_id,null,this);

        accountdetails_transactions_groupby_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                groupTransactionsBy = eGroupTransactionsBy.values()[position];
                getSupportLoaderManager().restartLoader(R.id.PdvTransactionResponse_Loader_id, null, AccountDetailsActivity.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        accountdetails_open_transactions_img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //toggle display of transactions by date
                toggleTransactionDisplay();
            }
        });

        accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
        accountdetails_cashflowchart.setTag("unselected");


        accountdetails_cashflowchart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //toggle display of transactions by date
                toggleCashflowDisplay();
            }
        });


    }


    public void initialiseLineChart (){

        //1. setup the chart settings
        accountdetails_cashflowchart_linechart = (LineChart) findViewById(R.id.accountdetails_cashflowchart_linechart);
        //accountdetails_cashflowchart_linechart = new LineChart(this);
        accountdetails_cashflowchart_linechart.setDescription(getString(R.string.accountdetails_cashflow_linechart_description));
        accountdetails_cashflowchart_linechart.setDescriptionTextSize(Float.valueOf(getString(R.string.ewise_chart_description_text_size)).floatValue());
        accountdetails_cashflowchart_linechart.setNoDataText(getString(R.string.accountdetails_cashflow_linechart_nodatatext));
        accountdetails_cashflowchart_linechart.setTouchEnabled(true);
        accountdetails_cashflowchart_linechart.setDragEnabled(true);
        accountdetails_cashflowchart_linechart.setScaleEnabled(true);
        accountdetails_cashflowchart_linechart.setDrawGridBackground(false);
        accountdetails_cashflowchart_linechart.setPinchZoom(true);
        //accountdetails_cashflowchart_linechart.setMaxVisibleValueCount(12);

        accountdetails_cashflowchart_linechart.setHighlightPerDragEnabled(true); //not sure about this!

        //other useful stuff - not used for now
        //accountdetails_cashflowchart_linechart.setBackgroundColor();


        //create the data entries for the chart - there will be two lines (dual y axis)
        //1st Y axis - cashflow
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 10945.50f));
        entries.add(new Entry(1, 34444.5f));
        entries.add(new Entry(2, 33333.50f));
        entries.add(new Entry(3, 41111f));
        entries.add(new Entry(4, 55555f));
        entries.add(new Entry(5, 101101f));

        //2nd y axis - account balance
        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 1105555));
        entries2.add(new Entry(1, 423445));
        entries2.add(new Entry(2, -333445));
        entries2.add(new Entry(3, -444445));
        entries2.add(new Entry(4, 666995));
        entries2.add(new Entry(5, 999995));

        //create the datasets using the data entries
        //1. cashflow data set
        LineDataSet dataSetCashflow = new LineDataSet(entries, getString(R.string.accountdetails_cashflow_linechart_cashflow_label));
        dataSetCashflow.setLineWidth(Float.valueOf(getString(R.string.ewise_chart_line_width)).floatValue());
        dataSetCashflow.setColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartCashflowLine));
        dataSetCashflow.setValueTextColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartCashflowLine));
        dataSetCashflow.setValueTextSize(Float.valueOf(getString(R.string.ewise_chart_text_size)).floatValue());
        dataSetCashflow.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSetCashflow.setDrawValues(false);
        dataSetCashflow.setDrawVerticalHighlightIndicator(true);

        //2. account balance data set
        LineDataSet dataSetBalance = new LineDataSet(entries2, getString(R.string.accountdetails_cashflow_linechart_balance_label));
        dataSetBalance.setLineWidth(Float.valueOf(getString(R.string.ewise_chart_line_width)).floatValue());
        dataSetBalance.setColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartBalanceLine));
        dataSetBalance.setValueTextColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartBalanceLine));
        dataSetBalance.setValueTextSize(Float.valueOf(getString(R.string.ewise_chart_text_size)).floatValue());
        dataSetBalance.setAxisDependency(YAxis.AxisDependency.RIGHT);
        dataSetBalance.setDrawValues(false);
        dataSetBalance.setDrawVerticalHighlightIndicator(true);


        //create the dataset list to be added to the chart data
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(dataSetCashflow);
        dataSets.add(dataSetBalance);

        //create the chartdata using the two data sets
        LineData lineData = new LineData(dataSets);

        //set the chart data to the chart
        accountdetails_cashflowchart_linechart.setData(lineData);

        //configure the chart legend
        Legend legend = accountdetails_cashflowchart_linechart.getLegend();
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        legend.setTextColor(ContextCompat.getColor(this, R.color.coloreWiseSecondaryTextBlack));

        //configure the x axis
        XAxis x = accountdetails_cashflowchart_linechart.getXAxis();
        x.setTextColor(ContextCompat.getColor(this, R.color.coloreWiseMainTextBlack));
        x.setTextSize(Float.valueOf(getString(R.string.ewise_chart_text_size)));
        x.setDrawGridLines(false);
        x.setAvoidFirstLastClipping(true);
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setLabelRotationAngle(315f);
        x.setAxisLineColor(ContextCompat.getColor(this, R.color.coloreWiseMainTextBlack));
        x.setGranularity(1f);

        //we have to draw the relevant data string in the x-axis representing each float value in x-axis
        final String[] x_axis_text = new String[] { /* value = 1 */ "Jan-16",
                                                    /* value = 2 */ "Feb-16",
                                                    /* value = 3 */ "Mar-16",
                                                    /* value = 4 */ "Apr-16",
                                                    /* value = 5 */ "May-16",
                                                    /* value = 6 */ "Jun-16"};
        AxisValueFormatter formatter = new AxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return x_axis_text[(int) value];
            }

            // we don't draw numbers, so no decimal digits needed
            @Override
            public int getDecimalDigits() {  return 0; }
        };

        x.setValueFormatter(formatter);

        //configure the left Y axis (cashflow)
        YAxis y_Cashflow = accountdetails_cashflowchart_linechart.getAxisLeft();
        y_Cashflow.setTextColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartCashflowLine));
        y_Cashflow.setTextSize(Float.valueOf(getString(R.string.ewise_chart_axis_text_size)));
        //y_Cashflow.setAxisMaxValue(100f);//temp set y axis max - can reset later with real data
        y_Cashflow.setDrawGridLines(true);
        y_Cashflow.setGridColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartCashflowLine));
        y_Cashflow.setGridLineWidth(Float.valueOf(getString(R.string.ewise_chart_gridline_width)));
        y_Cashflow.setAxisLineColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartCashflowLine));



        //configure the right Y axis (balance)
        YAxis y_Balance = accountdetails_cashflowchart_linechart.getAxisRight();
        y_Balance.setEnabled(true);//change later
        y_Balance.setTextColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartBalanceLine));
        y_Balance.setTextSize(Float.valueOf(getString(R.string.ewise_chart_axis_text_size)));
        //y_Balance.setAxisMaxValue(1100f);//temp set y axis max - can reset later with real data
        y_Balance.setDrawGridLines(true);
        y_Balance.setGridColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartBalanceLine));
        y_Balance.setGridLineWidth(Float.valueOf(getString(R.string.ewise_chart_gridline_width)));
        y_Balance.setAxisLineColor(ContextCompat.getColor(this, R.color.coloreWiseLineChartBalanceLine));


        //run an animation to draw the chart
        accountdetails_cashflowchart_linechart.animateXY(1000,2500, Easing.EasingOption.EaseInElastic, Easing.EasingOption.EaseInCubic);



    }


    /**
     * Gets a string array of 15 months, 1 months prior to current month and 3 months after current month
     * @return
     */
    public ArrayList<TransactionGroupByFilterDataObject> getGroupTransactionsByFilter(){

        ArrayList<TransactionGroupByFilterDataObject> viewObjectList = new ArrayList();

        for (int i = 0; i < 3; i++) {
            //TODO: find more suitable icons for filter
            int imageResId = (i == 0 ? R.drawable.ic_action_calendar_day : (i==1 ? R.drawable.ic_action_calendar_day : R.drawable.ic_action_calendar_day));
            viewObjectList.add(new TransactionGroupByFilterDataObject(imageResId, eGroupTransactionsBy.values()[i].toString()));
        }

        return viewObjectList;
    }


    /**
     * Gets a string array of 15 months, 1 months prior to current month and 3 months after current month
     * @return
     */
    public String[] getTransactionSelectFilter(){

        int numFilters = (groupTransactionsBy == eGroupTransactionsBy.DAY ? 1 : transactionCardsViewAdapter.getItemList().size()+1);
        String[] filterStrings = new String [numFilters];

        filterStrings[0] = "All";

        int pos = 1;

        if (groupTransactionsBy == eGroupTransactionsBy.DAY) { return filterStrings; }

        List<TransactionCardDataObject> list = transactionCardsViewAdapter.getItemList();

        for (TransactionCardDataObject transactionCard : list) {

            switch (groupTransactionsBy){
                case MONTH:
                    filterStrings[pos] = transactionCard.transactionMonth.toUpperCase(Locale.getDefault());
                    break;
                case YEAR:
                    filterStrings[pos] = transactionCard.transactionYear.toUpperCase(Locale.getDefault());
            }

            pos++;

        }

        return filterStrings;
    }


    /**
     * Gets a string array of 15 months, 1 months prior to current month and 3 months after current month
     * @return
     */
    public String[] getMonthsForFilter(){

        String[] monthsForFilter = new String [15];
        //get current date / month (for selected month in cashflow)
        Calendar cal = Calendar.getInstance();
        int pos = 0;
        cal.add(Calendar.MONTH, -11); //deduct 11 months
        for (int i = 0; i <= 14; i++) {
            String monthToAdd;
            if (i<=11) {
                monthToAdd = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + String.format(Locale.getDefault(), "-%d", cal.get(Calendar.YEAR));
            }
            else
            {
                monthToAdd = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + String.format(Locale.getDefault(), "-%d!", cal.get(Calendar.YEAR));
            }
            Log.d("**TRACE-calendar**", String.format("adding month: %s", monthToAdd));
            monthsForFilter[pos] = monthToAdd;
            pos++;
            cal.add(Calendar.MONTH, 1); //add a month

        }
        return monthsForFilter;
    }

    @Override
    protected void onResume() {

        super.onResume();


    }

    public void onClose(View view)
    {
        this.finish();
    }

    @Override
    public Loader<PdvTransactionResponse> onCreateLoader(int id, Bundle args) {
        return new PdvAccountTransactionResponseLoader(AccountDetailsActivity.this, _account);
    }


    @Override
    public void onLoadFinished(Loader<PdvTransactionResponse> loader, PdvTransactionResponse data) {

        Log.d("**TXN**LOAD***", String.format("Callback fired: AccountDetailsActivity.onLoadFinished() : %s", data.toString()));

        TransactionCardListDataObject transactionList = new TransactionCardListDataObject(getApplicationContext(), data, _account, groupTransactionsBy);
        transactionCardsViewAdapter.swapData(transactionList.get_transactionCardList());
        accountdetails_open_transactions_img.setImageResource(transactionCardsViewAdapter.get_showTransactions() == View.VISIBLE ? R.drawable.ic_action_folder_open : R.drawable.ic_action_folder_closed);


        //accountdetails_transaction_select_spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> monthArrayAdapter = new ArrayAdapter <String>(this, R.layout.month_select_spinner_item, getTransactionSelectFilter());
        accountdetails_transaction_select_spinner.setAdapter(monthArrayAdapter);

        //create the linechart
        initialiseLineChart();

        Log.d("**TRACE:NOTIFY ALL***", "Callback fired: Notify changed");

    }

    @Override
    public void onLoaderReset(Loader<PdvTransactionResponse> loader) {
        transactionCardsViewAdapter.resetData();
    }


    public void toggleTransactionDisplay(){

        try {

            transactionCardsViewAdapter.set_showTransactions(transactionCardsViewAdapter.get_showTransactions() == View.VISIBLE ? View.GONE : View.VISIBLE);
            accountdetails_open_transactions_img.setImageResource(transactionCardsViewAdapter.get_showTransactions() == View.VISIBLE ? R.drawable.ic_action_folder_open : R.drawable.ic_action_folder_closed);
            //unselect the cashflow chart
            if (accountdetails_cashflowchart.getTag().equals("selected")){
                accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
                accountdetails_cashflowchart.setTag("unselected");
                findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.GONE);
                this.accountdetails_cashflowchart_linechart.invalidate();
                this.accountdetails_transactioncard_recycler_view.setVisibility(View.VISIBLE);
            }


        }
        catch (Exception e)
        {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(),e.getMessage(),sMethod,"");
        }
    }

    public void toggleCashflowDisplay(){

        try {
            //TODO: hide transactions and display cashflow chart
            //transactionCardsViewAdapter.set_showTransactions(transactionCardsViewAdapter.get_showTransactions() == View.VISIBLE ? View.GONE : View.VISIBLE);
            int background = R.drawable.ewise_background_circle_secondary;

            if (accountdetails_cashflowchart.getTag().equals("unselected")){
                accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_selected);
                accountdetails_cashflowchart.setTag("selected");
                findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.VISIBLE);
                accountdetails_cashflowchart_linechart.animateXY(2000,2000, Easing.EasingOption.EaseInOutBack, Easing.EasingOption.EaseInOutBack);
                this.accountdetails_transactioncard_recycler_view.setVisibility(View.GONE);
            } else {
                accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
                accountdetails_cashflowchart.setTag("unselected");
                findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.GONE);
                accountdetails_cashflowchart_linechart.invalidate();
                this.accountdetails_transactioncard_recycler_view.setVisibility(View.VISIBLE);
            }
            accountdetails_cashflowchart.setImageResource(R.drawable.moneyapp_cashflow_chart);

        }
        catch (Exception e)
        {
            String sMethod = this.toString();
            sMethod = sMethod + Thread.currentThread().getStackTrace()[2].getMethodName() + "() ";
            generalExceptionHandler(e.getClass().getName(),e.getMessage(),sMethod,"");
        }
    }


/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mSwiping = false;
                //onSwipeDown();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mSwiping) {
                    //onSwipeUp(); //if action recognized as swipe then swipe
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();
                float xDelta = Math.abs(x - mDownX);
                float yDelta = Math.abs(y - mDownY);

                if (yDelta > mSlop && yDelta / 2 > xDelta) {
                    mSwiping = true;
                    return true;
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
*/

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    @Override
    public void onShowPress(MotionEvent event) {
        Log.d("***TRACE-Gesture***", "onShowPress: " + event.toString());
    }

    private void generalExceptionHandler (String eType, String eMessage, String eMethod, String eObjectString){
        String sFormat = getApplicationContext().getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Log.d("***TRACE-Gesture***", "onLongPress: " + event.toString());

    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        Log.d("***TRACE-Gesture***", "onSingleTapUp: " + event.toString());

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown();
                    } else {
                        onSwipeUp();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                }
            } else {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeDown();
                    } else {
                        onSwipeUp();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Log.d("***TRACE-Gesture***","onDown: " + event.toString());
        View view = findViewById(R.id.accountdetails_cardview);
        view.setVisibility(View.VISIBLE);

        return true;
    }


    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeUp() {
        Log.d("***TRACE-Gesture***", "onSwipeUp: ");
        this.accountdetails_cardview.setVisibility(View.GONE);
    }

    public void onSwipeDown() {
        Log.d("***TRACE-Gesture***", "onSwipeDown: ");
        this.accountdetails_cardview.setVisibility(View.VISIBLE);
    }


    /**
     * LoaderManager callback routine for PdvTransactionResponse data
     */
    private LoaderManager.LoaderCallbacks<PdvTransactionResponse> loaderCallbacks = new LoaderManager.LoaderCallbacks<PdvTransactionResponse>(){

        @Override
        public Loader<PdvTransactionResponse> onCreateLoader(int id, Bundle args) {
            return new PdvAccountTransactionResponseLoader(AccountDetailsActivity.this, _account);
        }


        @Override
        public void onLoadFinished(Loader<PdvTransactionResponse> loader, PdvTransactionResponse data) {

            TransactionCardListDataObject transactionList = new TransactionCardListDataObject(getApplicationContext(), data, _account, groupTransactionsBy);
            transactionCardsViewAdapter.swapData(transactionList.get_transactionCardList());
        }

        @Override
        public void onLoaderReset(Loader<PdvTransactionResponse> loader) {
            transactionCardsViewAdapter.resetData();
        }

    };
}
