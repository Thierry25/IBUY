package com.example.ibuy.models;

import java.util.List;

/**
 * Implements the response expected from API call
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class ProductServiceResponse {
    private List<Product> products;
    private final Throwable error;
    private Product product;


    public ProductServiceResponse(List<Product> products) {
        this.products = products;
        this.error = null;
        this.product = null;
    }

    public ProductServiceResponse(Throwable error) {
        this.error = error;
        this.products = null;
        this.product = null;
    }
    public List<Product> getProducts() {
        return products;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public ProductServiceResponse(Product product) {
        this.product = product;
        this.products = null;
        this.error = null;
    }

    public Throwable getError() {
        return error;
    }
}
