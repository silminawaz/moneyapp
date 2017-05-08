package com.ewise.moneyapp.adapters;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 14/4/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ewise.moneyapp.Fragments.PermissionsFragment;
import com.ewise.moneyapp.Fragments.SettingsFragment;

import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SettingsMenuPagerAdapter extends RemovableFragmentPagerAdapter {

    public static final int TAB_POSITION_SETTINGS = 0;
    public static final int TAB_POSITION_PERMISSIONS = 1;


    public SettingsMenuPagerAdapter(FragmentManager fm, List<String> pageTitleList) {
        super(fm, pageTitleList);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch(position){
            case TAB_POSITION_SETTINGS:
                return SettingsFragment.newInstance(position);
            case TAB_POSITION_PERMISSIONS:
                return PermissionsFragment.newInstance(position);
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence pageTitle = super.getPageTitle(position).toString();
        if (pageTitle.toString().equals("")) {
            switch (position) {
                case TAB_POSITION_SETTINGS:
                    return "Settings";
                case TAB_POSITION_PERMISSIONS:
                    return "Permissions";
                default:
                    return "";
            }
        }

        return pageTitle;
    }
}

