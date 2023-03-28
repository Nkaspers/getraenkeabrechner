package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.tcblauweiss.getraenkeabrechner.R;
import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Member;
import com.tcblauweiss.getraenkeabrechner.ui.entries.EntriesDiffCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class LastEntriesAdapter extends RecyclerView.Adapter<LastEntriesAdapter.ViewHolder> {
    private List<Entry> entryList;

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
        this.entryList = new ArrayList<>();
    }

    public void addEntryToTop(final List<Entry> entries){
        entryList = entries;
        notifyItemInserted(0);
    }

    public void submitList(List<Entry> entries) {
        final EntriesDiffCallback entriesDiffCallback = new EntriesDiffCallback(this.entryList, entries);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(entriesDiffCallback);

        this.entryList.clear();
        this.entryList.addAll(entries);
        diffResult.dispatchUpdatesTo(this);
    }

    public void filterByMember(Member member) {
        Predicate<Entry> memberPredicate = entry -> entry.getLastName().equals(member.getLastName()) && entry.getFirstName().equals(member.getFirstName());
        List<Entry> tempFilteredEntryList = entryList.stream().filter(memberPredicate).collect(Collectors.toList());
        submitList(tempFilteredEntryList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_viewholder, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getDateTextView().setText(entryList.get(position).getDateCreatedString());
        viewHolder.getNameTextView().setText(entryList.get(position).getName());
        viewHolder.getItemTextView().setText(entryList.get(position).getItemName());
        viewHolder.getPriceTextView().setText(entryList.get(position).getItemPriceString());
        viewHolder.getAmountTextView().setText(entryList.get(position).getAmountString());
        viewHolder.getTotalPriceTextView().setText(entryList.get(position).getTotalPriceString());
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }


}
