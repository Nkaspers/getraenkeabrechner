package com.tcblauweiss.getraenkeabrechner.ui.mainactivity.itemselection;

import androidx.recyclerview.widget.DiffUtil;

import com.tcblauweiss.getraenkeabrechner.model.Item;
import com.tcblauweiss.getraenkeabrechner.model.ItemWrapper;

import java.util.List;

public class ItemWrapperDiffCallback extends DiffUtil.Callback {
    private final List<ItemWrapper> oldItemWrapperList;
    private final List<ItemWrapper> newItemWrapperList;

    public ItemWrapperDiffCallback(List<ItemWrapper> oldItemWrapperList, List<ItemWrapper> newItemWrapperList) {
        this.oldItemWrapperList = oldItemWrapperList;
        this.newItemWrapperList = newItemWrapperList;
    }

    @Override
    public int getOldListSize() {
        return oldItemWrapperList.size();
    }

    @Override
    public int getNewListSize() {
        return newItemWrapperList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Item oldItem = oldItemWrapperList.get(oldItemPosition).getItem();
        Item newItem = newItemWrapperList.get(newItemPosition).getItem();

        return oldItem.hashCode() == newItem.hashCode();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ItemWrapper oldItemWrapper = oldItemWrapperList.get(oldItemPosition);
        ItemWrapper newItemWrapper = newItemWrapperList.get(newItemPosition);

        return oldItemWrapper.hashCode()  == newItemWrapper.hashCode();
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
