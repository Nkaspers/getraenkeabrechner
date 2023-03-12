package com.tcblauweiss.getraenkeabrechner.model;

import java.util.ArrayList;
import java.util.Collections;
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
        try {
            return Collections.frequency(itemList, item);}
        catch(NullPointerException e) {
            return 0;
        }
    }

    public ArrayList<ItemWrapper> getItemsAndCount(){
        Set<Item> itemSet = new HashSet<>(itemList);
        ArrayList<ItemWrapper> itemsAndCount = new ArrayList<>();
        for (Item item : itemSet) {
            int count = getNumberOfItems(item);
            itemsAndCount.add(new ItemWrapper(item, count));
        }
        return itemsAndCount;
    }
}

