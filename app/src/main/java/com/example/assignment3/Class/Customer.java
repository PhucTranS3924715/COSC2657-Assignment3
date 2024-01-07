package com.example.assignment3.Class;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer extends User implements Serializable {
    private int rewardPoint;
    private List<Voucher> voucherIDs;


    public Customer(String name, String phone, String email) {
        super(name, phone, email);
        this.rewardPoint = 0;
        this.voucherIDs = new ArrayList<>();
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
