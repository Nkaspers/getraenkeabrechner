package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.tcblauweiss.getraenkeabrechner.util.StringFormatter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@Entity
public class Entry {

    @CsvBindByName(column = "id", required = true)
    @PrimaryKey(autoGenerate = true)
    private long id;

    @CsvDate("yyyy.MM.dd HH:mm")
    @CsvBindByName(column = "datum", required = true)
    @ColumnInfo(name="date_created")
    private final Date dateCreated;

    @CsvBindByName(column = "nachname", required = true)
    @ColumnInfo(name="last_name")
    private final String lastName;

    @CsvBindByName(column = "vorname", required = true)
    @ColumnInfo(name="first_name")
    private final String firstName;

    @CsvBindByName(column = "getraenk", required = true)
    @ColumnInfo(name="item_name")
    private final String itemName;

    @CsvBindByName(column = "preis", required = true)
    @ColumnInfo(name="item_price")
    private final double itemPrice;

    @CsvBindByName(column = "anzahl", required = true)
    @ColumnInfo(name="amount")
    private final int amount;

    @CsvBindByName(column = "gesamtpreis", required = true)
    @ColumnInfo(name="total_price")
    private final double totalPrice;

    public Entry(Date dateCreated, String lastName, String firstName, String itemName, double itemPrice, int amount, double totalPrice) {
        this.dateCreated = dateCreated;
        this.lastName = lastName;
        this.firstName = firstName;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public String getDateCreatedString() {
        LocalDateTime dateTime = this.dateCreated.toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
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
