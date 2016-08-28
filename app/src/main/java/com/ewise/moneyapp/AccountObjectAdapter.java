package com.ewise.moneyapp;

import android.view.*;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.content.Context;

import com.ewise.moneyapp.dataobjects.AccountObject;

import java.util.ArrayList;
/**
 * Created by SilmiNawaz on 24/8/16.
 */


public class AccountObjectAdapter extends ArrayAdapter<AccountObject> {
    public AccountObjectAdapter(Context context, ArrayList<AccountObject> accounts) {
        super(context, 0, accounts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        AccountObject account = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.accountobject_item, parent, false);
        }
        // Lookup view for data population
        TextView accountname = (TextView) convertView.findViewById(R.id.accountname);
        TextView accountcurrency = (TextView) convertView.findViewById(R.id.accountcurrency);
        TextView accountbalance = (TextView) convertView.findViewById(R.id.accountbalance);
        // Populate the data into the template view using the data object
        accountname.setText(account.name);
        accountcurrency.setText(account.currency);
        accountbalance.setText(account.balance);
        // Return the completed view to render on screen
        return convertView;
    }
}