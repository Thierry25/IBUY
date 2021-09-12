package com.example.ibuy.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Cart implements Serializable {

    public Cart() {
    }

    private int product_id;
    private int quantity;

    private int price;
    private String name;
    @Exclude
    private int id;

    public int getProduct_id() {
        return product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    @Exclude
    public int getId() {
        return id;
    }

    @Exclude
    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
