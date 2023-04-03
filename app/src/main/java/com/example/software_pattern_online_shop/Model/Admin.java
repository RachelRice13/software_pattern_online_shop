package com.example.software_pattern_online_shop.Model;

public class Admin extends User {
    String jobTitle, employeeNumber;

    public Admin() {}

    public Admin(String firstName, String surname, String email, String password, String jobTitle, String employeeNumber) {
        super(firstName, surname, email, password);
        this.jobTitle = jobTitle;
        this.employeeNumber = employeeNumber;
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
}
