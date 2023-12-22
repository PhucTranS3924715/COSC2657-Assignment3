package com.example.assignment3;

import java.io.Serializable;

public class Driver extends User implements Serializable {
    private double earning;
    private int totalTrips;
    private double totalDistance;
    private String status;
    private double reputationPoint;
    private String carID;

    public Driver() {}

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

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }
}
