package com.example.store;

public class Company {

    public String id;
    public String companyName;
    public String companyEmail;
    public String password;
    public String products;
    public String companyPhone;

    public Company(String id, String companyName, String companyEmail, String companyPhone, String password, String products) {
        this.id = id;
        this.companyName = companyName;
        this.companyEmail = companyEmail;
        this.companyPhone = companyPhone;
        this.password = password;
        this.products = products;
    }


}
