package com.example.assignment3.Class;

public class PaymentMethod {
    private String name;
    private int icon;
    private String detail;

    public PaymentMethod(String name, int icon) {
        this.name = name;
        this.icon = icon;
        this.detail = "";
    }

    public PaymentMethod(String name, int icon, String detail) {
        this.name = name;
        this.icon = icon;
        this.detail = detail;
    }

    public PaymentMethod() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
