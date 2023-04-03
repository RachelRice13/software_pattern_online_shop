package com.example.software_pattern_online_shop.Model;

public class Customer extends User {
    String address, paymentMethod;

    public Customer() {}

    public Customer(String firstName, String surname, String email, String password, String address, String paymentMethod) {
        super(firstName, surname, email, password);
        this.address = address;
        this.paymentMethod = paymentMethod;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
