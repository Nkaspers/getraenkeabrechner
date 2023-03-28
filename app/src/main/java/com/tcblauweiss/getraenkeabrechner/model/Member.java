package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Member {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="first_name")
    private String firstName;
    @ColumnInfo(name="last_name")
    private String lastName;

    public Member(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
