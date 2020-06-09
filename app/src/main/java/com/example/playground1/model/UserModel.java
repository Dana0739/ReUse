package com.example.playground1.model;

public class UserModel {

    private int id;

    private String name;

    private String address;

    private String phone;

    private String email;

    private String password;

    public UserModel() {
    }

    public UserModel(int id, String name, String address, String phone, String email, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserModel setId(int id) {
        this.id = id;
        return this;
    }

    public UserModel setName(String name) {
        this.name = name;
        return this;
    }

    public UserModel setAddress(String address) {
        this.address = address;
        return this;
    }

    public UserModel setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserModel setEmail(String email) {
        this.email = email;
        return this;
    }

    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }
}
