package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.tcblauweiss.getraenkeabrechner.util.StringFormatter;

import java.util.Locale;

@Entity
public class Item {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="item_name")
    private String name;
    @ColumnInfo(name="price")
    private double price;
    @ColumnInfo(name="category")
    private int category;

    public Item(String name, double price, int category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCategory() {
        return category;
    }

    public String getPriceString(){

        return StringFormatter.formatToCurrencyString(price);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
