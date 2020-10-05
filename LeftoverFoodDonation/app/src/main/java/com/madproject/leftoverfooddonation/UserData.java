package com.madproject.leftoverfooddonation;

public class UserData{
    private String name,phone,email,password;
    public UserData() {
    }

    //Used for registration
    public UserData(String name, String phone, String email, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    //Used for displaying food donors
    public UserData(String name, String phone, String email) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
