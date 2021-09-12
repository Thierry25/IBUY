package com.example.ibuy.api;

import com.example.ibuy.utils.Utils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Represent Retrofit Singleton pattern
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */


public class RetrofitService {

    private static ProductService productService;

    public static ProductService getService() {
        if (productService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Utils.BASE_URL)
                    .build();

            productService = retrofit.create(ProductService.class);
        }

        return productService;
    }
}
