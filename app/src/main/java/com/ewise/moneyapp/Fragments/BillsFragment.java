package com.ewise.moneyapp.Fragments;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewise.moneyapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class BillsFragment extends MoneyAppFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "BillsFragment";

    private static final String ARG_SECTION_NUMBER = "section_number";

    public BillsFragment() {
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BillsFragment newInstance(int sectionNumber) {
        Log.d(TAG, "newInstance() - START");

        BillsFragment fragment = new BillsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() - START");
        View rootView = inflater.inflate(R.layout.fragment_bills, container, false);
        rootView.findViewById(R.id.billsFragmentTopLayout).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.billsFragmentTopLayout).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());

    }

    public void updatePageData(){
        if (isAdded()){
            Log.d(TAG, "TODO: Must implement updatePageData()");
        }
    }

}