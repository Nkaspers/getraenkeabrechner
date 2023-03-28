package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.tcblauweiss.getraenkeabrechner.util.StringFormatter;

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
    private final double itemPrice;
    @ColumnInfo(name="amount")
    private final int amount;
    @ColumnInfo(name="total_price")
    private final double totalPrice;

    public Entry(long dateCreated, String lastName, String firstName, String itemName, double itemPrice, int amount, double totalPrice) {
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
        return lastName + ",  " + firstName;
    }

    public double getItemPrice() {
        return itemPrice;
    }
    public String getItemPriceString() {

        return StringFormatter.formatToCurrencyString(itemPrice);
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getTotalPriceString(){
        return StringFormatter.formatToCurrencyString(totalPrice);
    }

    public void setId(long id) {
        this.id = id;
    }
}
