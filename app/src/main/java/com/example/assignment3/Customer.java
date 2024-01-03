package com.example.assignment3;

import java.io.Serializable;
import java.util.List;

public class Customer extends User implements Serializable {
    private int rewardPoint;
    private List<Voucher> voucherIDs;


    public Customer(String name, String phone, String email) {
        super(name, phone, email);
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
}
