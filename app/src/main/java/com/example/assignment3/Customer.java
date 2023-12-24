package com.example.assignment3;

import java.io.Serializable;
import java.util.List;

public class Customer extends User implements Serializable {
    private int rewardPoint;
    private List<String> voucherIDs;  // TODO: Missing Voucher class

    public Customer(String name, String phone, String address, String gender, String profilePicture, int rewardPoint, List<String> voucherIDs) {
        super(name, phone, address, gender, profilePicture);
        this.rewardPoint = rewardPoint;
        this.voucherIDs = voucherIDs;
    }

    public Customer(int rewardPoint, List<String> voucherIDs) {
        this.rewardPoint = rewardPoint;
        this.voucherIDs = voucherIDs;
    }

    public Customer() {}

    public int getRewardPoint() {
        return rewardPoint;
    }

    public void setRewardPoint(int rewardPoint) {
        this.rewardPoint = rewardPoint;
    }

    public List<String> getVoucherIDs() {
        return voucherIDs;
    }

    public void setVoucherIDs(List<String> voucherIDs) {
        this.voucherIDs = voucherIDs;
    }
}
