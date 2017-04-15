package com.ewise.moneyapp.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ewise.moneyapp.BillsFragment;
import com.ewise.moneyapp.R;

import java.util.List;

/**
 * Created by SilmiNawaz on 14/4/17.
 */
public class BillsMenuPagerAdapter extends RemovableFragmentPagerAdapter {

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */

    public BillsMenuPagerAdapter(FragmentManager fm, List<String> pageTitleList) {
        super(fm, pageTitleList);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        return BillsFragment.newInstance(position);

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence pageTitle = super.getPageTitle(position).toString();
        if (pageTitle.toString().equals("")) {
            return "Bills";
        }

        return "";
    }

}