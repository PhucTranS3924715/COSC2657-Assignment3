package com.example.assignment3;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phone;
    private String address;
    private String gender;
    private String profilePicture;

    public User(String name, String phone, String address, String gender, String profilePicture) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.profilePicture = profilePicture;
    }

    public User() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
