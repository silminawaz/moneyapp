package com.ewise.moneyapp;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class BillsFragment extends Fragment {
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
        BillsFragment fragment = new BillsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bills, container, false);
        return rootView;
    }
}