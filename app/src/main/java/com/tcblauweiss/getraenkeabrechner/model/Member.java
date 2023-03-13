package com.tcblauweiss.getraenkeabrechner.model;

public class Member {
    public String firstName;
    public String lastName;
    public int number;

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
