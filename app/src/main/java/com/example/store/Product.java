package com.example.store;

public class Product {

    public String id, productName, productDescription, productType, productPrice,productImage;

    public Product(String id, String productName, String productDescription, String productType, String productPrice,String productImage) {
        this.id = id;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productType = productType;
        this.productPrice = productPrice;
        this.productImage = productImage;
    }

    public Product(){

    }

}
