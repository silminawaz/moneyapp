package com.ewise.moneyapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ewise.moneyapp.R;
import com.ewise.moneyapp.data.TransactionGroupByFilterDataObject;

import java.util.ArrayList;

/**
 * Created by SilmiNawaz on 16/9/16.
 */
public class TransactionGroupByFilterAdapter extends ArrayAdapter<TransactionGroupByFilterDataObject> {

    Context _context;
    LayoutInflater inflater;
    ArrayList<TransactionGroupByFilterDataObject> itemsList;
    TransactionGroupByFilterViewHolder viewHolder = null;


    public TransactionGroupByFilterAdapter(Context context, int groupByFilterLayoutResourceId, ArrayList<TransactionGroupByFilterDataObject> itemsList) {
        super(context, groupByFilterLayoutResourceId, itemsList);
        _context = context;
        inflater = ((Activity) context).getLayoutInflater();
        this.itemsList = itemsList;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //return super.getDropDownView(position, convertView, parent);
        return getTransactionGroupByFilterView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        return getTransactionGroupByFilterView(position, convertView, parent);
    }

    public View getTransactionGroupByFilterView (int position, View convertView, ViewGroup parent){
        TransactionGroupByFilterDataObject listItem = itemsList.get(position);
        View itemView = convertView;

        if (itemView == null){
            //create a custom viewholder for the data and set it as a tag
            viewHolder = new TransactionGroupByFilterViewHolder();
            itemView = inflater.inflate(R.layout.transactions_groupby_spinner_item, parent, false);
            viewHolder.image = (ImageView) itemView.findViewById(R.id.transactions_groupby_image);
            viewHolder.label = (TextView) itemView.findViewById(R.id.transactions_groupby_text);
            viewHolder.iconText = (TextView) itemView.findViewById(R.id.transactions_groupby_icon_text);
            itemView.setTag(viewHolder);
        }
        else{
            viewHolder = (TransactionGroupByFilterViewHolder) itemView.getTag();
        }

        viewHolder.label.setText(listItem.filterLabel);
        viewHolder.image.setBackgroundResource(listItem.filterIcon);
        viewHolder.iconText.setText(listItem.filterIconText);

        return itemView;
    }

    public static class TransactionGroupByFilterViewHolder {
        public ImageView image;
        public TextView iconText;
        public TextView label;
    }

    private void generalExceptionHandler(String eType, String eMessage, String eMethod, String eObjectString) {
        String sFormat = _context.getString(R.string.exception_format_string);
        Log.e("GeneralException", String.format(sFormat, eType, eMethod, eMessage, eObjectString));
    }
}
