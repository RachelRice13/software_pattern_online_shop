package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;

public class Customer extends User implements Serializable {
    String address, profileImagePath;
    PaymentMethod paymentMethod;

    public Customer() {}

    public Customer(String firstName, String surname, String email, String password, String address, String profileImagePath, PaymentMethod paymentMethod) {
        super(firstName, surname, email, password);
        this.address = address;
        this.profileImagePath = profileImagePath;
        this.paymentMethod = paymentMethod;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }
    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
