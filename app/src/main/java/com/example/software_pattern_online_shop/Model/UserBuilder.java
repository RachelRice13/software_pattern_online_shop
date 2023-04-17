package com.example.software_pattern_online_shop.Model;

public class UserBuilder {
    String firstName, surname, email, password, address, profileImagePath, jobTitle, employeeNumber;
    PaymentMethod paymentMethod;

    public UserBuilder(String firstName, String surname, String email, String password, String jobTitle, String employeeNumber) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.jobTitle = jobTitle;
        this.employeeNumber = employeeNumber;
    }

    public UserBuilder(String firstName, String surname, String email, String password, String address, String profileImagePath, PaymentMethod paymentMethod) {
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.address = address;
        this.profileImagePath = profileImagePath;
        this.jobTitle = jobTitle;
        this.employeeNumber = employeeNumber;
        this.paymentMethod = paymentMethod;
    }

    public User createAdmin() {
        return new User(firstName, surname, email, password, jobTitle, employeeNumber);
    }

    public User createCustomer() {
        return new User(firstName, surname, email, password, address, profileImagePath, paymentMethod);
    }
}
