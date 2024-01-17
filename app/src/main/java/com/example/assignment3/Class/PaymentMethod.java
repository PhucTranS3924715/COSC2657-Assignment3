package com.example.assignment3.Class;

public class PaymentMethod {
    private String name;
    private int icon;

    public PaymentMethod(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }
}
