package com.ewise.moneyapp.Fragments;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 20/8/16.
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
public class BudgetsFragment extends MoneyAppFragment {

    private static final String TAG = "BudgetsFragment";
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public BudgetsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BudgetsFragment newInstance(int sectionNumber) {
        BudgetsFragment fragment = new BudgetsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budgets, container, false);
        //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

        rootView.findViewById(R.id.budgetsFragmentTopLayout).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.budgetsFragmentTopLayout).setPadding(0,0,0,getActivity().findViewById(R.id.tabs).getHeight());

    }

    public void updatePageData(){
        if (isAdded()){
            Log.d(TAG, "TODO: Must implement updatePageData()");
        }
    }
}