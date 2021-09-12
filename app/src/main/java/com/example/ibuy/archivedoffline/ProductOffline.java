//package com.example.ibuy.models;
//
//import androidx.room.Entity;
//import androidx.room.Index;
//import androidx.room.PrimaryKey;
//
//import com.google.gson.annotations.SerializedName;
//
///** Represents a product.
// * @author Thierry Marcelin & Loudwige Odice
// * @version 1.0
// * @since 2020-11-25
// */
//
//@Entity(tableName = "product_table",indices = @Index(value = {"product_id"},unique = true))
//public class Product {
//    @PrimaryKey
//    @SerializedName("id")
//    private int product_id;
//    @SerializedName("name")
//    private String name;
//    @SerializedName("stock")
//    private int stock;
//    @SerializedName("price")
//    private int price;
//    @SerializedName("img_url")
//    private String image_url;
//    @SerializedName("categories")
//    private String categories;
//
//    // db_related
//    private int cart_qty;
//    private int original_price;
//
//    /**
//     * Empty constructor of a product
//     */
//    public Product(){}
//
//    /**
//     * Constructs a product
//     * @param product_id an Integer that represents the id of the product
//     * @param name a String that represents the name/details related to that product
//     * @param stock an Integer that represents the amount of products available
//     * @param price an Integer that represents the price of a product
//     * @param image_url a String that represents the URL of a product
//     * @param categories a String that represents the categories of a product
//     */
//
//    public Product(int product_id, String name, int stock, int price, String image_url, String categories) {
//        this.product_id = product_id;
//        this.name = name;
//        this.stock = stock;
//        this.price = price;
//        this.image_url = image_url;
//        this.categories = categories;
//    }
//
//    /**
//     * Gets the product specific id
//     * @return an Integer representing the product id
//     */
//
//    public int getProduct_id() {
//        return product_id;
//    }
//
//    /**
//     * Sets the id of the product
//     * @param product_id an Integer containing the product id
//     */
//
//    public void setProduct_id(int product_id) {
//        this.product_id = product_id;
//    }
//
//    /**
//     * Gets the product details
//     * @return a String representing the product details
//     */
//    public String getName() {
//        return name;
//    }
//
//    /**
//     * Sets the product details
//     * @param name a String representing the product details
//     */
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    /**
//     * Gets the available stock of a product
//     * @return an Integer representing the stock available
//     */
//
//    public int getStock() {
//        return stock;
//    }
//
//    /**
//     * Sets the available stock of a product
//     * @param stock an Integer representing the available amount of a product
//     */
//
//    public void setStock(int stock) {
//        this.stock = stock;
//    }
//
//    /**
//     * Gets the price of a product
//     * @return an Integer representing the price of a product
//     */
//
//    public int getPrice() {
//        return price;
//    }
//
//    /**
//     * Sets the price of a product
//     * @param price an Integer representing the price of a product
//     */
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
//
//    /**
//     * Gets the url a product's picture
//     * @return a String representing the url of the product's picture
//     */
//
//    public String getImage_url() {
//        return image_url;
//    }
//
//    /**
//     * Sets the url of a product's picture
//     * @param image_url a String representing the url of the product's picture
//     */
//    public void setImage_url(String image_url) {
//        this.image_url = image_url;
//    }
//
//    /**
//     * Gets the categories of a product
//     * @return a String representing the url of the product's picture
//     */
//    public String getCategories() {
//        return categories;
//    }
//
//    /**
//     * Sets the categories of a product
//     * @param categories a String representing the categories of a product
//     */
//    public void setCategories(String categories) {
//        this.categories = categories;
//    }
//
//    /**
//     * Gets the quantity of a product in a user's shopping cart
//     * @return an Integer representing the quantity
//     */
//    public int getCart_qty() {
//        return cart_qty;
//    }
//
//    /**
//     * Sets the quantity of a product in a user's shopping cart
//     * @param cart_qty an Integer representing the quantity
//     */
//
//    public void setCart_qty(int cart_qty) {
//        this.cart_qty = cart_qty;
//    }
//
//    public int getOriginal_price() {
//        return original_price;
//    }
//
//    public void setOriginal_price(int original_price) {
//        this.original_price = original_price;
//    }
//}
