package com.ewise.moneyapp.Utils;

import android.accounts.Account;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v4.app.FragmentManager;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.ReportIssueFragment;
import com.ewise.moneyapp.adapters.HelpMenuPagerAdapter;
import com.ewise.moneyapp.adapters.PermissionsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.RemovableFragmentPagerAdapter;
import com.ewise.moneyapp.adapters.SectionsPagerAdapter;
import com.ewise.moneyapp.adapters.SettingsMenuPagerAdapter;
import com.ewise.moneyapp.adapters.TransferMenuPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SilmiNawaz on 15/4/17.
 */
public class FragmentPagerAdapterHelper {


    public static ViewPager setViewPagerToAdapter (ViewPager viewPager, Activity activity, FragmentManager fm, Class pagerAdapterClass, boolean keepIfExisting){

        if (keepIfExisting){
            if (viewPager.getAdapter()!=null) {
                if (viewPager.getAdapter().getClass().equals(pagerAdapterClass)) {
                    return viewPager; //do nothing if we are currently on same adapter
                }
            }
        }

        if (viewPager.getAdapter()!=null)
        {
            //clear it
            PagerAdapter adapter = viewPager.getAdapter();
            if (adapter.getClass().isInstance(RemovableFragmentPagerAdapter.class)){
                ((RemovableFragmentPagerAdapter)adapter).removeAllItems(viewPager);
                adapter=null;//GC the adapter since we are recreating each time
                viewPager.setAdapter(null); //viewpager to kill itself
                viewPager.notifyAll();
                viewPager.invalidate();
            }
            else
            {
                return null; /*must be using RemovableFragmentPagerAdapter class */
            }
        }

        //todo: create a new section pager adapter with the title array
        try {

            RemovableFragmentPagerAdapter adapter =
                    (RemovableFragmentPagerAdapter)
                            Class.forName(pagerAdapterClass.getName())
                                    .getConstructor()
                                    .newInstance(fm, getTitleList(pagerAdapterClass, activity));

            viewPager.setOffscreenPageLimit(1);
            viewPager.setAdapter(adapter);
            return viewPager;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public static List<String> getTitleList (Class pagerAdapterClass, Activity activity){
        String resourceName = pagerAdapterClass.getName().replaceAll("com.ewise.moneyapp.adapters", "");
        int resId = activity.getResources().getIdentifier(resourceName, "array", activity.getPackageName());
        List<String> titleList = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(resId)));
        return  titleList;
    }

}
