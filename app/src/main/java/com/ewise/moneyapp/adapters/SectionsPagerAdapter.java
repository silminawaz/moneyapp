package com.ewise.moneyapp.adapters;

/**
 * Created by SilmiNawaz on 15/4/17.
 */

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ewise.moneyapp.AccountsFragment_;
import com.ewise.moneyapp.BudgetsFragment;
import com.ewise.moneyapp.NetworthFragment;
import com.ewise.moneyapp.PlaceholderFragment;
import com.ewise.moneyapp.ProvidersFragment;
import com.ewise.moneyapp.R;

import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends RemovableFragmentPagerAdapter {

    public static final int TAB_POSITION_PROVIDERS = 0;
    public static final int TAB_POSITION_NETWORTH  = 1;
    public static final int TAB_POSITION_ACCOUNTS  = 2;
    public static final int TAB_POSITION_SPENDING  = 3;

    public SectionsPagerAdapter(FragmentManager fm, List<String> pageTitleList) {
        super(fm, pageTitleList);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        if (position == TAB_POSITION_ACCOUNTS) {
            //return the AccountFragment class
            //return AccountFragment.newInstance (position + 1);
            return AccountsFragment_.newInstance();
        }
        else if (position == TAB_POSITION_PROVIDERS){
            //todo: return the providers fragment
            return ProvidersFragment.newInstance(position);

        }
        else if (position == TAB_POSITION_NETWORTH){
            return NetworthFragment.newInstance(position);
        }
        else if (position == TAB_POSITION_SPENDING){
            return BudgetsFragment.newInstance(position);
        }
        else{
            return PlaceholderFragment.newInstance(position + 1);
        }

    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence pageTitle = super.getPageTitle(position).toString();
        if (pageTitle.toString().equals("")){
            /* if you dont want hardcoded string titles, pass the pageTitleList to the adapter when creating it */
            switch (position) {
                case TAB_POSITION_PROVIDERS:
                    return "Providers";
                case TAB_POSITION_NETWORTH:
                    return "Networth";
                case TAB_POSITION_ACCOUNTS:
                    return "Accounts";
                case TAB_POSITION_SPENDING:
                    return "Spending";
            }
        }

        return "";
    }

}


