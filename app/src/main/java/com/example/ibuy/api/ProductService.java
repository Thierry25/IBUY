package com.example.ibuy.api;

import com.example.ibuy.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/** Represents an interface to get all products.
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */
public interface ProductService {

    /**
     * @GET declares an HTTP GET request
     */
    @GET("all")
    Call<List<Product>> getAllProducts();

    @GET("category/fashion")
    Call<List<Product>> getFashionProducts();

    @GET("category/men")
    Call<List<Product>> getMenProducts();

    @GET("category/women")
    Call<List<Product>> getWomenProducts();

    @GET("category/footwear")
    Call<List<Product>> getFootwearProducts();

    /**
     * @GET declares an HTTP GET request
     * @Path("keyword") annotation on the value entered by the user marks it as a
     * replacement for the {keyword} placeholder in the @GET path
     */
    @GET("search/{keyword}")
    Call<List<Product>> searchProducts(@Path("keyword") String keyword);

    @GET("detail/{id}")
    Call<List<Product>> getProductDetails(@Path("id") int id);

}
