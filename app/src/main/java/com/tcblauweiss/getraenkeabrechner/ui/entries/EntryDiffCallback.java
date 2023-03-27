package com.tcblauweiss.getraenkeabrechner.ui.entries;

import androidx.recyclerview.widget.DiffUtil;

import com.tcblauweiss.getraenkeabrechner.model.Entry;
import com.tcblauweiss.getraenkeabrechner.model.Member;

import java.util.List;

public class EntryDiffCallback extends DiffUtil.Callback {
    private final List<Entry> oldEntryList;
    private final List<Entry> newEntryList;

    public EntryDiffCallback(List<Entry> oldEntryList, List<Entry> newEntryList){
        this.oldEntryList = oldEntryList;
        this.newEntryList = newEntryList;
    }
    @Override
    public int getOldListSize() {
        return oldEntryList.size();
    }

    @Override
    public int getNewListSize() {
        return newEntryList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEntryList.get(oldItemPosition).getId() == newEntryList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldEntryList.get(oldItemPosition).getId() == newEntryList.get(newItemPosition).getId();
    }
}
