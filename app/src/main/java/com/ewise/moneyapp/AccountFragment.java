package com.ewise.moneyapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.CardView;

import com.ewise.moneyapp.databinding.AccountDataBinding;
import com.ewise.moneyapp.dataobjects.AccountObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private static final String ARG_PARAM2 = "param2";

    private int mSectionNumber;
    //private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber Parameter 1.
     //* @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    public static AccountFragment newInstance(int sectionNumber /*, String param2 */) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        ArrayList<AccountObject> accounts = new ArrayList<AccountObject>();
        AccountObjectAdapter adapter = new AccountObjectAdapter(this.getContext(),accounts);
        ListView AccountListView = (ListView) view.findViewById(R.id.accountlist);
        AccountListView.setAdapter(adapter);

        //load CASH acccounts
        Log.d("Accounts", "Adding CASH accounts");
        AccountObject account = new AccountObject("1", "1201", "Smart Bank", "CASH", "XXXX-XXXX-1234", "My Savings", "SGD", "1200.50", "1200.50", "");
        AccountObject account1 = new AccountObject("2", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12341", "My Savings 1", "SGD", "1200.50", "1200.50", "");
        AccountObject account2 = new AccountObject("3", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12342", "My Savings 2", "SGD", "1300.50", "1300.50", "");
        AccountObject account3 = new AccountObject("4", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12343", "My Savings 3", "SGD", "1400.50", "1400.50", "");
        AccountObject account4 = new AccountObject("5", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12344", "My Savings 4", "SGD", "1500.50", "1500.50", "");
        AccountObject account5 = new AccountObject("6", "1201", "Smart Bank", "CASH", "XXXX-XXXX-1234", "My Savings 5", "SGD", "1200.50", "1200.50", "");
        AccountObject account6 = new AccountObject("7", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12341", "My Savings 6", "SGD", "1200.50", "1200.50", "");
        AccountObject account7 = new AccountObject("8", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12342", "My Savings 7", "SGD", "1300.50", "1300.50", "");
        AccountObject account8 = new AccountObject("9", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12343", "My Savings 8", "SGD", "1400.50", "1400.50", "");
        AccountObject account9 = new AccountObject("10", "1201", "Smart Bank", "CASH", "XXXX-XXXX-12344", "My Savings 9", "SGD", "1500.50", "1500.50", "");

        adapter.add (account);
        adapter.add (account1);
        adapter.add (account2);
        adapter.add (account3);
        adapter.add (account4);
        adapter.add (account5);
        adapter.add (account6);
        adapter.add (account7);
        adapter.add (account8);
        adapter.add (account9);

        //resize cardview based on the number of accounts
        if (adapter.getCount() < 4){
            CardView cardView = (CardView) view.findViewById(R.id.card_view_cash);
            ViewGroup.LayoutParams layoutparams = cardView.getLayoutParams();
            layoutparams.height = (200 + (adapter.getCount()* 150));
        }
        else
        {
            CardView cardView = (CardView) view.findViewById(R.id.card_view_cash);
            ViewGroup.LayoutParams layoutparams = cardView.getLayoutParams();
            layoutparams.height = (200 + (4 * 150));
        }

        //Load credit cards
        Log.d("Accounts", "Adding CREDIT accounts");
        ArrayList<AccountObject> accounts_credit = new ArrayList<AccountObject>();
        AccountObjectAdapter adapter_credit = new AccountObjectAdapter(this.getContext(),accounts_credit);
        ListView CreditAccountListView = (ListView) view.findViewById(R.id.accountlist_credit);
        CreditAccountListView.setAdapter(adapter_credit);

        AccountObject account_credit_1 = new AccountObject("11", "1201", "Smart Bank", "CREDIT", "XXXX-XXXX-1234", "Visa platinum", "SGD", "1200.50", "1200.50", "");
        AccountObject account_credit_2 = new AccountObject("11", "1201", "Smart Bank", "CREDIT", "XXXX-XXXX-1234", "Mastercard platinum", "SGD", "1200.50", "1200.50", "");
        AccountObject account_credit_3 = new AccountObject("11", "1201", "Smart Bank", "CREDIT", "XXXX-XXXX-1234", "Amex Gold", "SGD", "1200.50", "1200.50", "");

        adapter_credit.add (account_credit_1);
        adapter_credit.add (account_credit_2);
        adapter_credit.add (account_credit_3);
        adapter_credit.add (account_credit_3);


        //resize cardview based on the number of accounts
        //TODO: Implement dependency injection for this code
        int count = adapter_credit.getCount();
        if ( count < 4){
            CardView cardView = (CardView) view.findViewById(R.id.card_view_credit);
            ViewGroup.LayoutParams layoutparams = cardView.getLayoutParams();
            layoutparams.height = (200 + (adapter_credit.getCount()* 150));
        }


        //Load loan accounts
        Log.d("Accounts", "Adding LOAN accounts");
        ArrayList<AccountObject> accounts_loan = new ArrayList<AccountObject>();
        AccountObjectAdapter adapter_loan = new AccountObjectAdapter(this.getContext(),accounts_loan);
        ListView LoanAccountListView = (ListView) view.findViewById(R.id.accountlist_loan);
        LoanAccountListView.setAdapter(adapter_loan);

        AccountObject account_loan_1 = new AccountObject("12", "1201", "Smart Bank", "LOAN", "XXXX-XXXX-123412", "Mortgage", "SGD", "1200.50", "1200.50", "");
        AccountObject account_loan_2 = new AccountObject("13", "1201", "Smart Bank", "CREDIT", "XXXX-XXXX-123413", "Car loan", "SGD", "1200.50", "1200.50", "");
        AccountObject account_loan_3 = new AccountObject("14", "1201", "Smart Bank", "CREDIT", "XXXX-XXXX-123414", "Personal loan", "SGD", "1200.50", "1200.50", "");

        adapter_loan.add (account_loan_1);
        adapter_loan.add (account_loan_2);
        adapter_loan.add (account_loan_3);


        //TODO: Implement dependency injection for this code
        count = adapter_loan.getCount();
        if ( count < 4){
            CardView cardView = (CardView) view.findViewById(R.id.card_view_loan);
            ViewGroup.LayoutParams layoutparams = cardView.getLayoutParams();
            layoutparams.height = (200 + (adapter_loan.getCount()* 150));
        }

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onAccountFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAccountFragmentInteraction(Uri uri);
    }
}
