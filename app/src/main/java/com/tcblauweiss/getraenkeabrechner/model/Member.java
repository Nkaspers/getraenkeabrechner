package com.tcblauweiss.getraenkeabrechner.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Member {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name="first_name")
    public String firstName;
    @ColumnInfo(name="last_name")
    public String lastName;

    public Member(String firstname, String lastname){
        this.firstName = firstname;
        this.lastName = lastname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
