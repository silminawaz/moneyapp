package com.ewise.moneyapp.views;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2017 eWise Singapore. Created  on 30/8/16.
 */
public abstract class RecyclerViewAdapterBase<T, V extends RecyclerViewItemLayoutView<T>> extends RecyclerView.Adapter<RecyclerViewWrapper<V>> {

    protected List<T> items = new ArrayList<T>();

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public final RecyclerViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewWrapper<V>(onCreateItemView(parent, viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerViewWrapper<V> viewHolder, int position) {
        RecyclerViewItemLayoutView<T> view = viewHolder.getView();
        T bindDataObject = items.get(position);

        view.bind(bindDataObject);
    }

    // additional methods to manipulate the items
    public void setItemList(List<T> itemList)
    {
        items = itemList;
        if (items.size()>0){
            this.notifyItemRangeChanged(0, items.size());
        }
    }

    public void swapData(List<T> newData){
        Log.d("**TXN**LOAD***", String.format("RecyclerAdapter swapping data size : %s", String.format("%d",newData.size())));

        items.clear();
        items.addAll(newData);
        notifyDataSetChanged();
    }

    public void resetData(){
        items.clear();
        items = new ArrayList<T>();
        notifyDataSetChanged();
    }
}
