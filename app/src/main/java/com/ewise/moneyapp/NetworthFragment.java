package com.ewise.moneyapp;

/**
 * Created by SilmiNawaz on 20/8/16.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class NetworthFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    LinearLayout welcomeLayout;

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
        welcomeLayout = (LinearLayout) rootView.findViewById(R.id.providerWelcomeLayout);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MoneyAppApp app = (MoneyAppApp) getActivity().getApplication();

        //todo: uncomment after implementing net worth layout
        //welcomeLayout.setVisibility(app.isProviderFoundInDevice() ? View.GONE : View.VISIBLE);
    }
}