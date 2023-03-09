package com.tcblauweiss.getraenkeabrechner.model;

public class Item {
    private String name;
    private Float price;


    public Item(String name, Float price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Float getPrice() {
        return price;
    }
}
