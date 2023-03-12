package com.tcblauweiss.getraenkeabrechner.model;

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


    public float getTotal() {
        return getCount() * item.getPrice();
    }

    public Item getItem() {
        return item;
    }

    public String toString() {
        return getItem().getName() + ", Anzahl: " + getCount();
    }
}