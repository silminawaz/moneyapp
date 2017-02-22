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
import com.ewise.moneyapp.charts.CashflowLineChart;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.data.PdvTransactionResponse;
import com.ewise.moneyapp.data.TransactionCardDataObject;
import com.ewise.moneyapp.data.TransactionCardDataObject.eGroupTransactionsBy;
import com.ewise.moneyapp.data.TransactionCardListDataObject;
import com.ewise.moneyapp.data.TransactionGroupByFilterDataObject;
import com.ewise.moneyapp.loaders.PdvAccountTransactionResponseLoader;
import com.github.mikephil.charting.charts.LineChart;

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

    CashflowLineChart cashflowLineChart;

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


    public void initialiseLineChart () {

        //1. setup the chart settings
        accountdetails_cashflowchart_linechart = (LineChart) findViewById(R.id.accountdetails_cashflowchart_linechart);

        cashflowLineChart = new CashflowLineChart(this, accountdetails_cashflowchart_linechart, transactionCardsViewAdapter.getItemList(), groupTransactionsBy, 12);

        cashflowLineChart.animateChart(500, 500);

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

        int numFilters = (groupTransactionsBy == eGroupTransactionsBy.DAY ? 2 : transactionCardsViewAdapter.getItemList().size()+2);
        String[] filterStrings = new String [numFilters];

        filterStrings[0] = getString(R.string.transaction_filter_all);
        filterStrings[1] = getString(R.string.transaction_filter_forecast);

        int pos = 2;

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

            accountdetails_open_transactions_img.setBackgroundResource(R.drawable.ewise_background_rectangle_selected);

            if (transactionCardsViewAdapter.get_showTransactions() == View.VISIBLE)
            {
                transactionCardsViewAdapter.set_showTransactions (View.GONE);
                accountdetails_open_transactions_img.setImageResource(R.drawable.ic_action_folder_closed);
            }
            else
            {
                transactionCardsViewAdapter.set_showTransactions (View.VISIBLE);
                accountdetails_open_transactions_img.setImageResource(R.drawable.ic_action_folder_open);
            }

            //unselect the cashflow chart
            if (accountdetails_cashflowchart.getTag().equals("selected")){
                accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
                accountdetails_cashflowchart.setTag("unselected");
                findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.GONE);
                this.cashflowLineChart.reDraw();
                this.accountdetails_transactioncard_recycler_view.setVisibility(View.VISIBLE);
                accountdetails_open_transactions_img.setBackgroundResource(R.drawable.ewise_background_rectangle_selected);
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
                accountdetails_open_transactions_img.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
                accountdetails_cashflowchart.setTag("selected");
                findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.VISIBLE);
                cashflowLineChart.animateChart(500,500);
                this.accountdetails_transactioncard_recycler_view.setVisibility(View.GONE);
            } else {
                //accountdetails_cashflowchart.setBackgroundResource(R.drawable.ewise_background_rectangle_unselected);
                //accountdetails_cashflowchart.setTag("unselected");
                //findViewById(R.id.accountdetails_cashflowchart_layout).setVisibility(View.GONE);
                //cashflowLineChart.reDraw();
                //this.accountdetails_transactioncard_recycler_view.setVisibility(View.VISIBLE);
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
