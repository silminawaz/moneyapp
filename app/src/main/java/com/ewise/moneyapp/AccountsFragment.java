package com.ewise.moneyapp;

import android.app.Activity;
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

import com.ewise.android.pdv.api.model.PromptEntry;
import com.ewise.android.pdv.api.model.response.GetPromptsData;
import com.ewise.moneyapp.Fragments.MoneyAppFragment;
import com.ewise.moneyapp.Utils.PdvApiResults;
import com.ewise.moneyapp.Utils.PdvConnectivityCallback;
import com.ewise.moneyapp.data.AccountCardDataObject;
import com.ewise.moneyapp.data.AccountCardListDataObject;
import com.ewise.moneyapp.data.PdvAccountResponse;
import com.ewise.moneyapp.loaders.PdvAccountResponseLoader;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

@EFragment(R.layout.fragment_account_recycler)
public class AccountsFragment extends MoneyAppFragment implements MainActivity.FragmentUpdateListener {


    private OnFragmentInteractionListener mListener;

    @ViewById(R.id.account_recycler_view)
    RecyclerView account_recycler_view;

    @ViewById(R.id.accountsWelcomeLayout)
    LinearLayout welcomeLayout;

    @ViewById(R.id.pageLoadingLayout)
    LinearLayout pageLoadingLayout;


    private AccountCardsViewAdapter cardsViewAdapter;


    public AccountsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountsFragment.
     */
    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment_();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return null; //layout creation handled by AndroidAnnotations
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
                Log.d("AccountsFragment", "app.mustRestoreAccounts()");
                pageLoadingLayout.setVisibility(View.VISIBLE);
                app.pdvRestoreAllProviderAccounts((MainActivity)getActivity());
            } else {
                Log.d("AccountsFragment", "!app.mustRestoreAccounts() - going to run updatePageData");
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




    @Override
    public void refreshFragmentUI(){

        Log.d("AccountsFragment", "refreshFragment()");

        if (isAdded()) {
            Log.d("AccountsFragment", "refreshFragment() - isAdded()");

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //hide the login layout\
                    Log.d("AccountsFragment", "refreshFragment() - isAdded() - runOnUiThread.run()");
                    MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();
                    if (welcomeLayout == null) {
                        welcomeLayout = (LinearLayout) getActivity().findViewById(R.id.accountsWelcomeLayout);
                    }
                    if (welcomeLayout != null)
                        welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
                    updatePageData();
                }
            });
        }
    }

    public void updatePageData()
    {
        Log.d("AccountsFragment", "updatePageData()");

        pageLoadingLayout = (LinearLayout) getActivity().findViewById(R.id.pageLoadingLayout);
        MoneyAppApp app = (MoneyAppApp)getActivity().getApplication();
        Log.d("AccountsFragment", "updatePageData() - 1");
        AccountCardListDataObject accountCardListDO = app.getAccountCardListDO(getActivity());
        Log.d("AccountsFragment", "updatePageData() - 2");
        if (accountCardListDO!=null){
            Log.d("AccountsFragment", "updatePageData() - 3");
            List<AccountCardDataObject> cardDataList = accountCardListDO.getAccountCardList();
            Log.d("AccountsFragment", "updatePageData() - 4");
            if (cardDataList!=null){
                Log.d("AccountsFragment", "updatePageData() - cardDataList!=null; swapping data");
                AccountCardsViewAdapter cardsViewAdapter1 = new AccountCardsViewAdapter(getActivity());
                //cardsViewAdapter.swapData(cardDataList);
                cardsViewAdapter1.setItemList(cardDataList);
                account_recycler_view.setAdapter(null);
                account_recycler_view.setAdapter(cardsViewAdapter1);
                if (pageLoadingLayout!=null) {
                    pageLoadingLayout.setVisibility(View.GONE);
                }
            }
        }

    }


}
