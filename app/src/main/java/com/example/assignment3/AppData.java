package com.example.assignment3;

public class AppData {
    private static AppData instance;

    private String driverID;
    private String uidCustomer;

    private AppData() {
        // Private constructor to prevent instantiation
    }

    public static synchronized AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getUidCustomer() {
        return uidCustomer;
    }

    public void setUidCustomer(String uidCustomer) {
        this.uidCustomer = uidCustomer;
    }
}

