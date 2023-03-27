package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.ui.entries.EntryDiffCallback;

import java.util.ArrayList;
import java.util.List;


public class LastEntriesAdapter extends RecyclerView.Adapter<LastEntriesAdapter.ViewHolder> implements Filterable {
    //list that contains elements that are currently shown in recyclerview
    private List<Entry> localDataSetFiltered;
    //set once and used as a fallback when nothing is filtered
    private List<Entry> localDataSet;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView, nameTextView, itemTextView, priceTextView, amountTextView, totalPriceTextView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.text_last_entries_date);
            nameTextView = view.findViewById(R.id.text_last_entries_name);
            itemTextView = view.findViewById(R.id.text_last_entries_item);
            priceTextView = view.findViewById(R.id.text_last_entries_price);
            amountTextView = view.findViewById(R.id.text_last_entries_amount);
            totalPriceTextView = view.findViewById(R.id.text_last_entries_total);
        }

        public TextView getDateTextView(){
            return dateTextView;
        }

        public TextView getNameTextView(){
            return nameTextView;
        }

        public TextView getItemTextView() {
            return itemTextView;
        }

        public TextView getPriceTextView()  {
            return priceTextView;
        }

        public TextView getAmountTextView() {
            return amountTextView;
        }

        public TextView getTotalPriceTextView() {
            return totalPriceTextView;
        }
    }

    public LastEntriesAdapter() {
        this.localDataSet = new ArrayList<>();
        this.localDataSetFiltered = new ArrayList<>();
    }

    public void submitList(final List<Entry> entries){
        localDataSet = entries;
        EntryDiffCallback diffCallback = new EntryDiffCallback(localDataSetFiltered, entries);
        localDataSetFiltered = entries;
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this);
    }

    private void submitFilter(List<Entry> filteredEntries){
        EntryDiffCallback diffCallback = new EntryDiffCallback(localDataSetFiltered, filteredEntries);
        localDataSetFiltered = filteredEntries;
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_viewholder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getDateTextView().setText(localDataSetFiltered.get(position).getDateCreatedString());
        viewHolder.getNameTextView().setText(localDataSetFiltered.get(position).getName());
        viewHolder.getItemTextView().setText(localDataSetFiltered.get(position).getItemName());
        viewHolder.getPriceTextView().setText(localDataSetFiltered.get(position).getItemPriceString());
        viewHolder.getAmountTextView().setText(localDataSetFiltered.get(position).getAmountString());
        viewHolder.getTotalPriceTextView().setText(localDataSetFiltered.get(position).getTotalPriceString());
    }

    @Override
    public int getItemCount() {
        return localDataSetFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searchStr = charSequence.toString().toLowerCase();
                List<Entry> filteredEntries = new ArrayList<>();
                if(searchStr.isEmpty()) {
                    filteredEntries = localDataSet;
                }
                else {
                    for(Entry entry: localDataSet){
                        if(entry.getLastName().toLowerCase().contains(searchStr) ||
                                entry.getFirstName().toLowerCase().contains(searchStr)){
                            filteredEntries.add(entry);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredEntries;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                submitFilter((List<Entry>) filterResults.values);
            }
        };
    }
}
