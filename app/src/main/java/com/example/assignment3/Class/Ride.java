package com.example.assignment3.Class;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Ride implements Serializable {

    private GeoPoint DriverLocation;
    private GeoPoint DropPoint;
    private GeoPoint PickPoint;
    private String status;
    private String uidCustomer;
    private String uidDriver;

    public Ride(){}

    public GeoPoint getDriverLocation() {
        return DriverLocation;
    }

    public GeoPoint getDropPoint() {
        return DropPoint;
    }

    public GeoPoint getPickPoint() {
        return PickPoint;
    }

    public String getStatus() {
        return status;
    }

    public String getUidCustomer() {
        return uidCustomer;
    }

    public String getUidDriver() {
        return uidDriver;
    }
}


