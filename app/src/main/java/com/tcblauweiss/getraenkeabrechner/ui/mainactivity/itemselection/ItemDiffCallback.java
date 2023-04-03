package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import androidx.recyclerview.widget.DiffUtil;

import com.tcblauweiss.getraenkeabrechner.model.Item;

import java.util.List;

public class ItemDiffCallback extends DiffUtil.Callback {
    private final List<Item> oldItemList;
    private final List<Item> newItemList;

    public ItemDiffCallback(List<Item> oldItemList, List<Item> newItemList) {
        this.oldItemList = oldItemList;
        this.newItemList = newItemList;
    }

    @Override
    public int getOldListSize() {
        return oldItemList.size();
    }

    @Override
    public int getNewListSize() {
        return newItemList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Item oldItem = oldItemList.get(oldItemPosition);
        Item newItem = newItemList.get(newItemPosition);

        return oldItem.hashCode() == newItem.hashCode();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Item oldItem = oldItemList.get(oldItemPosition);
        Item newItem = newItemList.get(newItemPosition);

        return oldItem.getName().equals(newItem.getName());
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
