package com.example.assignment3.Class;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Driver extends User implements Serializable {
    private double earning;
    private int totalTrips;
    private double totalDistance;
    private String status;
    private double reputationPoint;
    private Car ownedCar;
    private GeoPoint location;

    public Driver() {}

    public Driver(String name, String phone, String email, String address, String gender, Car ownedCar) {
        super(name, phone, email, address, gender);
        this.earning = 0.0;
        this.totalTrips = 0;
        this.totalDistance = 0.0;
        this.reputationPoint = 5.0;
        this.ownedCar = ownedCar;
    }

    public double getEarning() {
        return earning;
    }

    public void setEarning(double earning) {
        this.earning = earning;
    }

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getReputationPoint() {
        return reputationPoint;
    }

    public void setReputationPoint(double reputationPoint) {
        this.reputationPoint = reputationPoint;
    }

    public Car getOwnedCar() {
        return ownedCar;
    }

    public void setOwnedCar(Car ownedCar) {
        this.ownedCar = ownedCar;
    }

    public void setDocumentId(String id) {
    }

    public String getDocumentId() {
        return null;
    }
}
