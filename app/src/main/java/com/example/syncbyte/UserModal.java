package com.example.syncbyte;

public class UserModal {

    String username;
    String empCode;
    String email;
    String number;
    String dob;
    String password;

    public UserModal(String username, String empCode, String email, String number, String dob, String password) {
        this.username = username;
        this.empCode = empCode;
        this.email = email;
        this.number = number;
        this.dob = dob;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getDob() {
        return dob;
    }

    public String getPassword() {
        return password;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
