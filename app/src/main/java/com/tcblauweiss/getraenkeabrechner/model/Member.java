package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.opencsv.bean.CsvBindByName;

@Entity
public class Member {


    @PrimaryKey(autoGenerate = true)
    private long id;

    @CsvBindByName( column = "vorname", required = true)
    @ColumnInfo(name="first_name")
    private String firstName;

    @CsvBindByName( column = "nachname", required = true)
    @ColumnInfo(name="last_name")
    private String lastName;

    @Ignore
    public Member(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Member(){}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return lastName + ", " + firstName;
    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
}
