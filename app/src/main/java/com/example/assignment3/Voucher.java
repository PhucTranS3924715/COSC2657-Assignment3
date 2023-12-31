package com.example.assignment3;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Voucher implements Serializable {
    private String voucherName;
    private double voucherDiscount;
    private String expirationDate;

    public Voucher(String voucherName, double voucherDiscount, String expirationDate) {
        this.voucherName = voucherName;
        this.voucherDiscount = voucherDiscount;
        this.expirationDate = expirationDate;
    }

    public Voucher() {}

    public String getVoucherName() {
        return voucherName;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
