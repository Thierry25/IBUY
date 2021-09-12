package com.example.ibuy.models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Represents an order.
 *
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class Order implements Serializable {

    // Firebase Purposes
    public Order() {
    }

    @Exclude
    private String order_id;
    private int price;
    private String status;
    private long created_date;
    private int product;
    @Exclude
    private String name;
    private String address;
    @Exclude
    private List<Cart> cartList;
    @Exclude
    private String user_id;
    @Exclude
    private String type;

    //TODO: Remember to define the above methods


    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }


    public long getCreated_date() {
        return created_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getProduct() {
        return product;
    }

    public void setProduct(int product) {
        this.product = product;
    }


    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
