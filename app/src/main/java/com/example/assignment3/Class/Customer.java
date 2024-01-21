package com.example.assignment3.Class;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User implements Serializable {
    private int rewardPoint;
    private List<Voucher> voucherIDs;
    private List<Ride> historyRides;
    private List<PaymentMethod> paymentMethods;
    private String sosPhone;
    private String sosMessage;

    public Customer(String name, String phone, String email) {
        super(name, phone, email);
        this.rewardPoint = 0;
        this.voucherIDs = new ArrayList<>();
        this.historyRides = new ArrayList<>();
        this.paymentMethods = new ArrayList<>();
        this.sosPhone = "";
        this.sosMessage = "";
    }

    public Customer() {}

    public int getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(int rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public List<Voucher> getVoucherIDs() {
        return voucherIDs;
    }

    public void setVoucherIDs(List<Voucher> voucherIDs) {
        this.voucherIDs = voucherIDs;
    }

    public List<Ride> getHistoryRides() {
        return historyRides;
    }

    public void setHistoryRides(List<Ride> historyRides) {
        this.historyRides = historyRides;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getSosPhone() {
        return sosPhone;
    }

    public void setSosPhone(String sosPhone) {
        this.sosPhone = sosPhone;
    }

    public String getSosMessage() {
        return sosMessage;
    }

    public void setSosMessage(String sosMessage) {
        this.sosMessage = sosMessage;
    }
}
