package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Entry;

import java.util.ArrayList;
import java.util.List;


public class LastEntriesAdapter extends RecyclerView.Adapter<LastEntriesAdapter.ViewHolder> {
    private List<Entry> localDataSet;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateTextView, nameTextView, itemTextView, priceTextView, amountTextView, totalPriceTextView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.text_lastentries_date);
            nameTextView = view.findViewById(R.id.text_lastentries_name);
            itemTextView = view.findViewById(R.id.text_lastentries_item);
            priceTextView = view.findViewById(R.id.text_lastentries_price);
            amountTextView = view.findViewById(R.id.text_lastentries_amount);
            totalPriceTextView = view.findViewById(R.id.text_lastentries_totalprice);
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
    }

    public void submitList(final List<Entry> entries){
        localDataSet = entries;
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_viewholder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getDateTextView().setText(localDataSet.get(position).getDateCreatedString());
        viewHolder.getNameTextView().setText(localDataSet.get(position).getName());
        viewHolder.getItemTextView().setText(localDataSet.get(position).getItemName());
        viewHolder.getPriceTextView().setText(localDataSet.get(position).getItemPriceString());
        viewHolder.getAmountTextView().setText(localDataSet.get(position).getAmountString());
        viewHolder.getTotalPriceTextView().setText(localDataSet.get(position).getTotalPriceString());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


}
