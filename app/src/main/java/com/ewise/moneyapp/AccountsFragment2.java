package com.ewise.moneyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ewise.moneyapp.Fragments.MoneyAppFragment;
import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.data.AccountCardListDataObject;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.loaders.PdvAccountResponseLoader;
import com.google.android.gms.vision.text.Line;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountsFragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountsFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AccountsFragment2 extends MoneyAppFragment implements MainActivity.FragmentUpdateListener {

    private final String TAG = "AccountsFragment2";

    private OnFragmentInteractionListener mListener;

    RecyclerView account_recycler_view;
    LinearLayout welcomeLayout;
    LinearLayout pageLoadingLayout;


    private AccountCardsViewAdapter cardsViewAdapter;


    public AccountsFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountsFragment.
     */
    public static AccountsFragment2 newInstance() {
        AccountsFragment2 fragment = new AccountsFragment2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView() - START");
        //selectedItemsList=new ArrayList<>();


        View rootView = inflater.inflate(R.layout.fragment_account_recycler, container, false);
        account_recycler_view= (RecyclerView) rootView.findViewById(R.id.account_recycler_view);
        welcomeLayout=(LinearLayout)rootView.findViewById(R.id.accountsWelcomeLayout);
        pageLoadingLayout=(LinearLayout)rootView.findViewById(R.id.pageLoadingLayout);

        //if there are any legitimate providers , we can hide the welcome layout.
        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        welcomeLayout.setVisibility(View.GONE);

        //set the fragment listener
        //((MainActivity)getActivity()).setProviderFragmentListener(this);

        Log.d(TAG, "onCreateView() - END");

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

        getActivity().findViewById(R.id.account_recycler_view).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());


        //Attach adapter and load the data
        account_recycler_view.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //cardsViewAdapter = new AccountCardsViewAdapter(this.getContext());
        //account_recycler_view.setAdapter(cardsViewAdapter);
        account_recycler_view.setAdapter(null);

        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);

        //((MainActivity)getActivity()).setAccountsFragmentListener(this);


        if (app.isProviderFoundInDevice() && app.pdvLoginStatus.isLoggedOnToPdv()) {
            if (app.mustRestoreAccounts()) {
                Log.d(TAG, "app.mustRestoreAccounts()");
                pageLoadingLayout.setVisibility(View.VISIBLE);
                app.pdvRestoreAllProviderAccounts((MainActivity)getActivity());
            } else {
                Log.d(TAG, "!app.mustRestoreAccounts() - going to run updatePageData");
                updatePageData();
            }
        }


        //getLoaderManager().initLoader(R.id.PdvAccountResponse_Loader_id, null, loaderCallbacks);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAccountsFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAccountsFragmentInteraction(Uri uri);
    }


    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {

        Intent intent= new Intent(this.getActivity(), AccountDetailsActivity.class);
        String message = "Hello world";
        intent.putExtra("com.wise.moneyapp.MESSAGE", message);
        startActivity(intent);
    }


    /**
     * LoaderManager callback routine for PdvAccountResponse data
     */
    private LoaderManager.LoaderCallbacks<PdvAccountResponse> loaderCallbacks = new LoaderManager.LoaderCallbacks<PdvAccountResponse>(){

        @Override
        public Loader<PdvAccountResponse> onCreateLoader(int id, Bundle args) {
            return new PdvAccountResponseLoader(getContext());
        }

        @Override
        public void onLoadFinished(Loader<PdvAccountResponse> loader, PdvAccountResponse data) {

            //todo: get preferred currency code from API (currently hard coded in resources)
            Currency ccy = Currency.getInstance(Locale.getDefault());
            String baseCurrency = getString(R.string.var_base_currency);
            AccountCardListDataObject cardList = new AccountCardListDataObject(getContext(), data, baseCurrency);
            List<AccountCardDataObject> cardDataList = cardList.getAccountCardList();
            cardsViewAdapter.swapData(cardDataList);
        }

        @Override
        public void onLoaderReset(Loader<PdvAccountResponse> loader) {
            cardsViewAdapter.resetData();
        }
    };



    public void updatePageData()
    {
        Log.d(TAG, "updatePageData()");

        if (isAdded()) {
            MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
            if (welcomeLayout == null) {
                welcomeLayout = (LinearLayout) getActivity().findViewById(R.id.accountsWelcomeLayout);
            }
            if (welcomeLayout != null) {
                welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
            }
            pageLoadingLayout = (LinearLayout) getActivity().findViewById(R.id.pageLoadingLayout);
            AccountCardListDataObject accountCardListDO = app.getAccountCardListDO(getActivity());
            if (accountCardListDO != null) {
                List<AccountCardDataObject> cardDataList = accountCardListDO.getAccountCardList();
                if (cardDataList != null) {
                    if (cardsViewAdapter == null) {
                        cardsViewAdapter = new AccountCardsViewAdapter(getActivity());
                        account_recycler_view.setAdapter(cardsViewAdapter);
                    }
                    cardsViewAdapter.swapData(cardDataList);
                    if (pageLoadingLayout != null) {
                        pageLoadingLayout.setVisibility(View.GONE);
                    }
                }
            }
        }

    }


}
