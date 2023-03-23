package com.tcblauweiss.getraenkeabrechner.model;

import com.tcblauweiss.getraenkeabrechner.util.StringFormatter;

public class ItemWrapper {
    private Item item;
    private int count;


    public ItemWrapper(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public int getCount() {
        return count;
    }


    public double getTotal() {
        return getCount() * item.getPrice();
    }

    public String getTotalString() {
        return StringFormatter.formatToCurrencyString(getTotal());
    }

    public Item getItem() {
        return item;
    }

    public String toString() {
        return getItem().getName() + ", Anzahl: " + getCount();
    }
}