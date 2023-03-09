package com.tcblauweiss.getraenkeabrechner.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class Entry {
    private LocalDateTime dateCreated;
    private String surname;
    private String forename;
    private Item item;
    private Integer amount;
    private Float totalPrice;

    public Entry(LocalDateTime dateCreated, String surname, String forename, Item item, Integer amount, Float totalPrice) {
        this.dateCreated = dateCreated;
        this.surname = surname;
        this.forename = forename;
        this.item = item;
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

    public String getSurname() {
        return surname;
    }

    public String getForename() {
        return forename;
    }

    public String getName(){
        String name = surname + ", " + forename;
        return name;
    }

    public Item getItem() {
        return item;
    }

    public Integer getAmount() {
        return amount;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }
}
