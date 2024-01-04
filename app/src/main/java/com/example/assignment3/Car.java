package com.example.assignment3;

import java.io.Serializable;

public class Car implements Serializable {
    private String model;
    private String licensePlate;
    private int seat;
    private String driverID;

    public Car() {}

    public Car(String model, String licensePlate, int seat, String driverID) {
        this.model = model;
        this.licensePlate = licensePlate;
        this.seat = seat;
        this.driverID = driverID;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }
}
