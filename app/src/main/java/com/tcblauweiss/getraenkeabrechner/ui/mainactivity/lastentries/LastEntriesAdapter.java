package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.lastentries;

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
    private List<Entry> displayedEntryList;
    private EntryClickedListener entryClickedListener;

    public interface EntryClickedListener {
        void onEntryClicked(Entry entry);
        void onEntryLongClicked(Entry entry);
    }
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
        this.entryList = new ArrayList<>();
        this.displayedEntryList = new ArrayList<>(entryList);
    }

    public void setEntryClickedListener(EntryClickedListener entryClickedListener){
        this.entryClickedListener = entryClickedListener;
    }
    public void submitList(final List<Entry> entries) {
        final EntriesDiffCallback entriesDiffCallback = new EntriesDiffCallback(this.entryList, entries);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(entriesDiffCallback);

        entryList = entries;
        displayedEntryList = new ArrayList<>(entryList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void filterByMember(Member member) {
        Predicate<Entry> memberPredicate = entry -> entry.getLastName().equals(member.getLastName()) && entry.getFirstName().equals(member.getFirstName());
        List<Entry> tempFilteredEntryList = entryList.stream().filter(memberPredicate).collect(Collectors.toList());

        final EntriesDiffCallback entriesDiffCallback = new EntriesDiffCallback(this.displayedEntryList, tempFilteredEntryList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(entriesDiffCallback);

        this.displayedEntryList.clear();
        this.displayedEntryList.addAll(tempFilteredEntryList);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.viewholder_entry, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Entry entry = displayedEntryList.get(position);
        viewHolder.getDateTextView().setText(entry.getDateCreatedString());
        viewHolder.getNameTextView().setText(entry.getName());
        viewHolder.getItemTextView().setText(entry.getItemName());
        viewHolder.getPriceTextView().setText(entry.getItemPriceString());
        viewHolder.getAmountTextView().setText(entry.getAmountString());
        viewHolder.getTotalPriceTextView().setText(entry.getTotalPriceString());

        if(entryClickedListener != null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    entryClickedListener.onEntryClicked(entry);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return displayedEntryList.size();
    }


}
