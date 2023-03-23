package com.tcblauweiss.getraenkeabrechner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Receipt {
    private ArrayList<Item> itemList;

    public Receipt() {
        itemList = new ArrayList<>();
    }

    public void addItem(Item item) {
        itemList.add(item);
    }

    public void removeItem(Item item) {
        itemList.remove(item);
    }

    public int getNumberOfItems(Item item) {
        if (itemList.contains(item)) {
            return Collections.frequency(itemList, item);
        } return 0;
    }

    public ArrayList<ItemWrapper> getItemsAndCount(){
        Set<Item> itemSet = new HashSet<>(itemList);
        ArrayList<ItemWrapper> itemsAndCount = new ArrayList<>();
        for (Item item : itemSet) {
            int count = getNumberOfItems(item);
            itemsAndCount.add(new ItemWrapper(item, count));
        }
        itemsAndCount.sort(new Comparator<ItemWrapper>() {
            @Override
            public int compare(ItemWrapper itemWrapper1, ItemWrapper itemWrapper2) {
                Item item1 = itemWrapper1.getItem();
                Item item2 = itemWrapper2.getItem();

                return item1.getName().compareTo(item2.getName());
            }
        });

        return itemsAndCount;
    }

    public void clearData() {
        itemList.clear();
    }
}

