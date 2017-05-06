package com.ewise.moneyapp.Fragments;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewise.moneyapp.MainActivity;
import com.ewise.moneyapp.MoneyAppApp;
import com.ewise.moneyapp.R;
import com.ewise.moneyapp.charts.NetworthAssetLiabilityPieChart;
import com.ewise.moneyapp.charts.NetworthHBarChart;
import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.data.AccountCardListDataObject;
import com.ewise.moneyapp.data.NetworthDataObject;
import com.ewise.moneyapp.data.NetworthDataObject.*;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class NetworthFragment extends MoneyAppFragment implements MainActivity.FragmentUpdateListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    LinearLayout welcomeLayout;
    LinearLayout netWorthLayout;

    //networth summary card field
    HorizontalBarChart networthSummaryBarChart;
    TextView netWorthSummaryHeaderAmount;
    TextView netWorthSummaryHeaderCurrency;
    TextView netWorthTotalAmount;
    TextView netWorthTotalCurrency;

    //networth asset card fields
    PieChart networthAssetsPieChart;
    TextView netWorthAssetsHeaderAmount;
    TextView netWorthAssetsHeaderCurrency;
    LinearLayout netWorthAssetsBreakdownLayout;
    TextView netWorthAssetsTotalAmount;
    TextView netWorthAssetsTotalCurrency;

    //networth liabilities card fields
    PieChart networthLiabilitiesPieChart;
    TextView netWorthLiabilitiesHeaderAmount;
    TextView netWorthLiabilitiesHeaderCurrency;
    LinearLayout netWorthLiabilitiesBreakdownLayout;
    TextView netWorthLiabilitiesTotalAmount;
    TextView netWorthLiabilitiesTotalCurrency;

    NetworthHBarChart networthHBarChart=null;
    NetworthAssetLiabilityPieChart assetsPieChart=null;
    NetworthAssetLiabilityPieChart liabilitiesPieChart=null;

    public NetworthFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NetworthFragment newInstance(int sectionNumber) {
        NetworthFragment fragment = new NetworthFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_networth, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        //todo: uncomment after implementing net worth layout
        welcomeLayout = (LinearLayout) rootView.findViewById(R.id.networthWelcomeLayout);
        netWorthLayout = (LinearLayout) rootView.findViewById(R.id.netWorthLayout);

        //networth summary card field
        networthSummaryBarChart = (HorizontalBarChart) rootView.findViewById(R.id.networthSummaryBarChart);
        netWorthTotalAmount = (TextView) rootView.findViewById(R.id.netWorthTotalAmount);
        netWorthTotalCurrency = (TextView) rootView.findViewById(R.id.netWorthTotalCurrency) ;
        netWorthSummaryHeaderAmount = (TextView) rootView.findViewById(R.id.netWorthSummaryHeaderAmount);
        netWorthSummaryHeaderCurrency = (TextView) rootView.findViewById(R.id.netWorthSummaryHeaderCurrency) ;

        //networth asset card fields
        networthAssetsPieChart = (PieChart) rootView.findViewById(R.id.networthAssetsPieChart);
        netWorthAssetsBreakdownLayout = (LinearLayout) rootView.findViewById(R.id.netWorthAssetsBreakdownLayout);
        netWorthAssetsTotalAmount = (TextView) rootView.findViewById(R.id.netWorthAssetsTotalAmount);
        netWorthAssetsTotalCurrency = (TextView) rootView.findViewById(R.id.netWorthAssetsTotalCurrency);
        netWorthAssetsHeaderAmount = (TextView) rootView.findViewById(R.id.netWorthAssetsHeaderAmount);
        netWorthAssetsHeaderCurrency = (TextView) rootView.findViewById(R.id.netWorthAssetsHeaderCurrency) ;

        //networth liabilities card fields
        networthLiabilitiesPieChart = (PieChart) rootView.findViewById(R.id.networthLiabilitiesPieChart);
        netWorthLiabilitiesBreakdownLayout = (LinearLayout) rootView.findViewById(R.id.netWorthLiabilitiesBreakdownLayout);
        netWorthLiabilitiesTotalAmount = (TextView) rootView.findViewById(R.id.netWorthLiabilitiesTotalAmount);
        netWorthLiabilitiesTotalCurrency = (TextView) rootView.findViewById(R.id.netWorthLiabilitiesTotalCurrency);
        netWorthLiabilitiesHeaderAmount = (TextView) rootView.findViewById(R.id.netWorthLiabilitiesHeaderAmount);
        netWorthLiabilitiesHeaderCurrency = (TextView) rootView.findViewById(R.id.netWorthLiabilitiesHeaderCurrency) ;

        rootView.findViewById(R.id.networthScrollView).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();


        //fragment specific menu creation
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.networthScrollView).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();

        //set the fragment listener
        //((MainActivity)getActivity()).setNetworthFragmentListener(this);


        if (app.isProviderFoundInDevice() && app.pdvLoginStatus.isLoggedOnToPdv()) {
            if (app.mustRestoreAccounts()) {
                app.pdvRestoreAllProviderAccounts((MainActivity)getActivity());
            } else {
                updatePageData();
            }
        }

        //get the card list and
        String baseCurrency = getString(R.string.var_base_currency);
        if (app.pdvAccountResponse.accounts != null) {
            AccountCardListDataObject cardList = new AccountCardListDataObject(getContext(), app.pdvAccountResponse, baseCurrency);

        }
    }

    @Override
    public void onResume() {

        super.onResume();
        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
        welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
        netWorthLayout.setVisibility(app.isProviderFoundInDevice() ? View.VISIBLE : View.GONE);

        updatePageData();


    }



    private void initializeNetworthSummaryChart(NetworthDataObject networthDO){

        networthSummaryBarChart = (HorizontalBarChart) getActivity().findViewById(R.id.networthSummaryBarChart);
        if (networthSummaryBarChart!=null) {
            if (networthHBarChart != null) {
                networthHBarChart = null;
            }
            networthHBarChart = new NetworthHBarChart(networthSummaryBarChart, networthDO, getContext());

        }

    }

    private void initializePieCharts(List<NetworthEntry> dataObject){
        if (dataObject.size()>0) {
            NetworthEntry entry = dataObject.get(0);
            switch (entry.getType()){
                case ASSET:
                    networthAssetsPieChart = (PieChart) getActivity().findViewById(R.id.networthAssetsPieChart);
                    if (networthAssetsPieChart != null) {
                        if (assetsPieChart != null) {
                            assetsPieChart = null;
                        }
                        assetsPieChart = new NetworthAssetLiabilityPieChart(networthAssetsPieChart, dataObject, getContext());
                        //
                    }
                    break;
                case LIABILITY:
                    networthLiabilitiesPieChart = (PieChart) getActivity().findViewById(R.id.networthLiabilitiesPieChart);
                    if (networthLiabilitiesPieChart != null) {
                        if (liabilitiesPieChart != null) {
                            liabilitiesPieChart = null;
                        }
                        liabilitiesPieChart = new NetworthAssetLiabilityPieChart(networthLiabilitiesPieChart, dataObject, getContext());
                        //
                    }
                    break;
            }
        }
    }


    private void createNetworthAssetLiabilityBreakdown (NetworthDataObject dataObject, eNetworthType networthType){

        if (dataObject==null) return;

        //todo: copy of asset breakdown , just change in list and layout used
        LinearLayout layout = null;
        List<NetworthEntry> networthEntries = null;

        if (networthType.equals(eNetworthType.ASSET)){
            layout = (LinearLayout) getActivity().findViewById(R.id.netWorthAssetsBreakdownLayout);
            if (dataObject.getAssetsList()!=null){
                networthEntries = dataObject.getAssetsList();
            }
        }
        else if (networthType.equals(eNetworthType.LIABILITY)){
            layout = (LinearLayout) getActivity().findViewById(R.id.netWorthLiabilitiesBreakdownLayout);
            if (dataObject.getLiabilitiesList()!=null){
                networthEntries = dataObject.getLiabilitiesList();
            }
        }
        else
        {
            return;
        }

        if (layout!=null){
            //clear existing breakdown elements
            layout.removeAllViewsInLayout();

            if (networthEntries.size()<=0) return;

            for (NetworthEntry entry : networthEntries){
                AccountCardDataObject accountCard = entry.getAccountCard();

                LinearLayout.LayoutParams entryLayoutParams = new LinearLayout.LayoutParams((ViewGroup.LayoutParams.WRAP_CONTENT), (ViewGroup.LayoutParams.WRAP_CONTENT));
                LinearLayout entryLayout = new LinearLayout(getActivity());
                entryLayout.setLayoutParams(entryLayoutParams);
                entryLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView label = new TextView(getActivity());
                LinearLayout.LayoutParams labelLayoutParams =
                        new LinearLayout.LayoutParams(
                                getActivity().getResources().getDimensionPixelSize(R.dimen.ewise_networth_summary_label_width),
                                (ViewGroup.LayoutParams.WRAP_CONTENT));
                label.setLayoutParams(labelLayoutParams);
                label.setText(accountCard.getTitle());
                label.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.ewise_secondary_text_size));
                entryLayout.addView(label);


                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(0);
                df.setMinimumFractionDigits(0);
                TextView amount = new TextView(getActivity());
                LinearLayout.LayoutParams amountLayoutParams =
                        new LinearLayout.LayoutParams(
                                (ViewGroup.LayoutParams.WRAP_CONTENT),
                                (ViewGroup.LayoutParams.WRAP_CONTENT));
                amount.setLayoutParams(amountLayoutParams);
                amount.setText(df.format(accountCard.preferredCurrencyBalance));
                amount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.ewise_secondary_text_size));
                entryLayout.addView(amount);


                TextView currency = new TextView(getActivity());
                LinearLayout.LayoutParams ccyLayoutParams =
                        new LinearLayout.LayoutParams(
                                (ViewGroup.LayoutParams.WRAP_CONTENT),
                                (ViewGroup.LayoutParams.WRAP_CONTENT));
                ccyLayoutParams.setMarginStart(Math.round(getActivity().getResources().getDimension(R.dimen.ewise_horizontal_margin)));
                currency.setLayoutParams(ccyLayoutParams);
                currency.setText(accountCard.preferredCurrencyCode);
                currency.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(R.dimen.ewise_secondary_text_size));
                entryLayout.addView(currency);

                //add entry layout to main breakdown layout
                layout.addView(entryLayout);
            }
        }

    }


    @Override
    public void refreshFragmentUI(){

        Log.d("AccountsFragment", "refreshFragment()");

        if (isAdded()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //accounts retrieval failed
                    updatePageData();
                }
            });
        }
    }


    public void updatePageData()
    {

        Log.d("NetworthFragment", "updatePageData() - START");

        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        AccountCardListDataObject accountCardListDO = app.getAccountCardListDO(getContext());
        if (accountCardListDO!=null){
            if (accountCardListDO.getAccountCardList()!=null){

                welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
                netWorthLayout.setVisibility(app.isProviderFoundInDevice() ? View.VISIBLE : View.GONE);

                Log.d("NetworthFragment", "updatePageData() - accountCardListDO.getAccountCardList()!=null");

                NetworthDataObject nwdo = new NetworthDataObject( getContext(), accountCardListDO, app.getBaseCurrency());
                //including building the various networth charts - if possible do this in the networthdataobject
                Log.d("NetworthFragment", "updatePageData() - totalAssetsAmount=" + nwdo.getTotalAssetsAmount());
                BigDecimal totalAssetsAmount = nwdo.getTotalAssetsAmount();
                Log.d("NetworthFragment", "updatePageData() - totalLiabilitiesAmount=" + nwdo.getTotalLiabilitiesAmount());
                BigDecimal totalLiabilitiesAmount = nwdo.getTotalLiabilitiesAmount();
                BigDecimal totalNetworthAmount = totalAssetsAmount.subtract(totalLiabilitiesAmount);
                Log.d("NetworthFragment", "updatePageData() - totalNetworthAmount=" + totalNetworthAmount);
                String currencyCode = nwdo.getCurrency().getCurrencyCode();

                DecimalFormat df = new DecimalFormat();
                df.setMaximumFractionDigits(0);
                df.setMinimumFractionDigits(0);
                df.setGroupingUsed(true);
                netWorthTotalAmount.setText(df.format(totalNetworthAmount)); //todo: formatting may be required for big decimal display
                netWorthTotalCurrency.setText(currencyCode);
                netWorthAssetsTotalAmount.setText(df.format(totalAssetsAmount));
                netWorthTotalAmount.setText(nwdo.getTotalAssetsAmount().toPlainString());
                netWorthAssetsTotalCurrency.setText(currencyCode);
                netWorthLiabilitiesTotalAmount.setText(df.format(totalLiabilitiesAmount));
                netWorthLiabilitiesTotalCurrency.setText(currencyCode);
                netWorthLiabilitiesTotalCurrency.setText(currencyCode);
                netWorthAssetsTotalCurrency.setText(currencyCode);
                netWorthSummaryHeaderAmount.setText(df.format(totalNetworthAmount));
                netWorthSummaryHeaderAmount.setText(df.format(totalNetworthAmount));
                int iCompareNetworth = Double.compare(totalNetworthAmount.doubleValue(), 0d);
                if (iCompareNetworth<0){
                    netWorthSummaryHeaderAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.coloreWiseHighlight));
                }
                else{
                    netWorthSummaryHeaderAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.coloreWisePrimary));
                }

                netWorthAssetsHeaderAmount.setText(df.format(totalAssetsAmount));
                netWorthAssetsHeaderAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.coloreWisePrimary));

                netWorthLiabilitiesHeaderAmount.setText(df.format(totalLiabilitiesAmount));
                netWorthLiabilitiesHeaderAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.coloreWiseHighlight));

                initializeNetworthSummaryChart(nwdo);
                initializePieCharts(nwdo.getAssetsList());
                initializePieCharts(nwdo.getLiabilitiesList());

                //createNetworthAssetBreakdown (nwdo);
                createNetworthAssetLiabilityBreakdown (nwdo, eNetworthType.ASSET);
                createNetworthAssetLiabilityBreakdown (nwdo, eNetworthType.LIABILITY);

            }

        }
        Log.d("NetworthFragment", "updatePageData() - END");


    }


}