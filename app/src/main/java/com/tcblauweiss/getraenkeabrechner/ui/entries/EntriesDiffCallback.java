package com.tcblauweiss.getraenkeabrechner.ui.entries;

import androidx.recyclerview.widget.DiffUtil;

import com.tcblauweiss.getraenkeabrechner.model.Entry;

import java.util.List;

public class EntriesDiffCallback extends DiffUtil.Callback {
    private final List<Entry> oldItemList;
    private final List<Entry> newItemList;

    public EntriesDiffCallback(List<Entry> oldItemList, List<Entry> newItemList) {
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
        return oldItemList.get(oldItemPosition).getId() == newItemList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemList.get(oldItemPosition).hashCode() == newItemList.get(newItemPosition).hashCode();
    }
}
