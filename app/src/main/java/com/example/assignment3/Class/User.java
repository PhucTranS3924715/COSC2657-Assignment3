package com.example.assignment3.Class;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String address;
    private String gender;
    private String profileImageUri;
    private GeoPoint location;

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = "";
        this.gender = "";
        this.profileImageUri = "";
    }

    public User(String name, String phone, String email, String address, String gender) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.gender = gender;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return profileImageUri;
    }

    public void setProfilePicture(String profilePicture) {
        this.profileImageUri = profilePicture;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }
}
