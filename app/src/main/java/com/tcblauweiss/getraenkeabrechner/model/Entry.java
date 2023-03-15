package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Entity
public class Entry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="date_created")
    private final long dateCreated;
    @ColumnInfo(name="last_name")
    private final String lastName;
    @ColumnInfo(name="first_name")
    private final String firstName;
    @ColumnInfo(name="item_name")
    private final String itemName;
    @ColumnInfo(name="item_price")
    private final float itemPrice;
    @ColumnInfo(name="amount")
    private final int amount;
    @ColumnInfo(name="total_price")
    private final float totalPrice;

    public Entry(long dateCreated, String lastName, String firstName, String itemName, float itemPrice, int amount, float totalPrice) {
        this.dateCreated = dateCreated;
        this.lastName = lastName;
        this.firstName = firstName;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public String getDateCreatedString() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateCreated), TimeZone.getDefault().toZoneId());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");
        String timeFormatted = dateTime.format(timeFormatter);
        String dateFormatted = dateTime.format(dateFormatter);
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

    public float getItemPrice() {
        return itemPrice;
    }
    public String getItemPriceString() {
        return String.valueOf(itemPrice);
    }

    public long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
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

    public void setId(long id) {
        this.id = id;
    }
}
