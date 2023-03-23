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

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
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
}
