package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Purchase implements Serializable {
    ArrayList<BasketItem> items;
    double totalPrice;
    String date;
    PaymentMethod paymentMethod;

    public Purchase(){}

    public Purchase(ArrayList<BasketItem> items, double totalPrice, String date, PaymentMethod paymentMethod) {
        this.items = items;
        this.totalPrice = totalPrice;
        this.date = date;
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<BasketItem> getItems() {
        return items;
    }
    public void setItems(ArrayList<BasketItem> items) {
        this.items = items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
