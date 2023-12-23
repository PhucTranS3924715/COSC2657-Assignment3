package com.example.assignment3;

import java.io.Serializable;
import java.util.List;

public class Customer extends User implements Serializable {
    private int rewardPoint;
    private List<String> voucherIDs;  // TODO: Missing Voucher class

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
