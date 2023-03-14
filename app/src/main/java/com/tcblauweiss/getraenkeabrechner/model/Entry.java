package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="date_created")
    private LocalDateTime dateCreated;
    @ColumnInfo(name="last_name")
    private String lastName;
    @ColumnInfo(name="first_name")
    private String firstName;
    @ColumnInfo(name="item")
    private Item item;
    @ColumnInfo(name="item_price")
    private float itemPrice;
    @ColumnInfo(name="amount")
    private int amount;
    @ColumnInfo(name="total_price")
    private float totalPrice;

    public Entry(LocalDateTime dateCreated, String lastName, String firstName, Item item, int amount, float totalPrice) {
        this.dateCreated = dateCreated;
        this.lastName = lastName;
        this.firstName = firstName;
        this.item = item;
        this.itemPrice = item.getPrice();
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    public String getDateCreated() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
        String timeFormatted = dateCreated.format(timeFormatter);
        String dateFormatted = dateCreated.format(dateFormatter);
        return timeFormatted + ", " + dateFormatted;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getName(){
        String name = lastName + ", " + firstName;
        return name;
    }

    public Item getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public String getAmountString(){
        return String.valueOf(amount);
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public String getTotalPriceString(){
        return String.valueOf(Math.round(totalPrice*100)/100) + "€";
    }

}
