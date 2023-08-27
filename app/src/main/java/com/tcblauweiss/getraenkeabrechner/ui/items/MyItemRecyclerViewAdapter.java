package com.tcblauweiss.getraenkeabrechner.ui.items;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcblauweiss.getraenkeabrechner.databinding.ViewholderItemsBinding;
import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection.ItemDiffCallback;
//import com.tcblauweiss.getraenkeabrechner.ui.items.databinding.FragmentAllItemsBinding;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<Item> itemList;

    public MyItemRecyclerViewAdapter() {
        itemList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(ViewholderItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    public void submitList(List<Item> items) {
        final ItemDiffCallback itemDiffCallback = new ItemDiffCallback(this.itemList, items);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(itemDiffCallback);

        this.itemList.clear();
        this.itemList.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.getNumberTextView().setText(String.valueOf(position));
        holder.getNameTextView().setText(item.getName());
        holder.getPriceTextView().setText(item.getPriceString());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView, numberTextView, priceTextView;

        public ViewHolder(ViewholderItemsBinding binding) {
            super(binding.getRoot());
            numberTextView = binding.itemNumber;
            nameTextView = binding.itemName;
            priceTextView = binding.itemPrice;
        }

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getNumberTextView() {
            return numberTextView;
        }

        public TextView getPriceTextView() {
            return priceTextView;
        }
    }
}