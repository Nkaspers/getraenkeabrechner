package com.tcblauweiss.getraenkeabrechner.model;

import java.util.Date;

public class Entry {
    private Date dateCreated;
    private String surname;
    private String forename;
    private Item item;
    private Integer amount;
    private Float totalPrice;

    public Entry(Date dateCreated, String surname, String forename, Item item, Integer amount, Float totalPrice) {
        this.dateCreated = dateCreated;
        this.surname = surname;
        this.forename = forename;
        this.item = item;
        this.amount = amount;
        this.totalPrice = totalPrice;
    }

    public String getDateCreated() {
        return dateCreated.toString();
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
