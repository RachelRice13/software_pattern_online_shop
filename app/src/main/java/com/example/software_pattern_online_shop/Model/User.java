package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;

public class User implements Serializable {
    String firstName, surname, email, password, address, profileImagePath, jobTitle, employeeNumber;
    PaymentMethod paymentMethod;

    public User() {}

    public User(String firstName, String surname, String email, String password, String jobTitle, String employeeNumber) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.jobTitle = jobTitle;
        this.employeeNumber = employeeNumber;
    }

    public User(String firstName, String surname, String email, String password, String address, String profileImagePath, PaymentMethod paymentMethod) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.address = address;
        this.profileImagePath = profileImagePath;
        this.paymentMethod = paymentMethod;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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

    public String getJobTitle() {
        return jobTitle;
    }
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
