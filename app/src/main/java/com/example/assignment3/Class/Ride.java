package com.example.assignment3.Class;

import com.google.firebase.firestore.DocumentSnapshot;
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

    public Ride(GeoPoint driverLocation, GeoPoint dropPoint, GeoPoint pickPoint, String status,
                String uidCustomer, String uidDriver) {
        DriverLocation = driverLocation;
        DropPoint = dropPoint;
        PickPoint = pickPoint;
        this.status = status;
        this.uidCustomer = uidCustomer;
        this.uidDriver = uidDriver;
    }

    public Ride (DocumentSnapshot document){
        this.uidDriver = document.getString("uidCustomer");
        this.uidCustomer = document.getString("uidCustomer");
        this.DriverLocation = document.getGeoPoint("DriverLocation");
        this.DropPoint = document.getGeoPoint("DropPoint");
        this.PickPoint = document.getGeoPoint("PickPoint");
        this.status = document.getString("status");
    }

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

    public void setDriverLocation(GeoPoint driverLocation) {
        DriverLocation = driverLocation;
    }

    public void setDropPoint(GeoPoint dropPoint) {
        DropPoint = dropPoint;
    }

    public void setPickPoint(GeoPoint pickPoint) {
        PickPoint = pickPoint;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUidCustomer(String uidCustomer) {
        this.uidCustomer = uidCustomer;
    }

    public void setUidDriver(String uidDriver) {
        this.uidDriver = uidDriver;
    }
}


