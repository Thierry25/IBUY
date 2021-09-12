package com.example.ibuy.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.api.ProductService;
import com.example.ibuy.api.RetrofitService;
import com.example.ibuy.models.Product;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Represents products related data
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class ProductRepository {

    private final ProductService productService;
    private final MutableLiveData<ProductServiceResponse> searchedProductLiveData = new MutableLiveData<>();
    private final MutableLiveData<ProductServiceResponse> productDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressbarObservable = new MutableLiveData<>();
    private int productStock;
    private int productPrice;
    private String productName;

    public ProductRepository(){
        productService = RetrofitService.getService();
    }

    // All Products
    public LiveData<ProductServiceResponse> getAllProducts(){
        MutableLiveData<ProductServiceResponse> productsLiveData = new MutableLiveData<>();
        productService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                progressbarObservable.setValue(false);
                if(response.isSuccessful()){
                    productsLiveData.postValue(new ProductServiceResponse(response.body()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                progressbarObservable.setValue(false);
                productsLiveData.postValue(new ProductServiceResponse(t));
            }
        });
        return productsLiveData;
    }

    // Fashion Products
    public LiveData<ProductServiceResponse> getFashionProducts(){
         MutableLiveData<ProductServiceResponse> fashionProductsLiveData = new MutableLiveData<>();
         productService.getFashionProducts().enqueue(new Callback<List<Product>>() {
             @Override
             public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                 fashionProductsLiveData.postValue(new ProductServiceResponse(response.body()));
             }

             @Override
             public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                fashionProductsLiveData.postValue((new ProductServiceResponse(t)));
             }
         });
         return fashionProductsLiveData;
    }

    // Men Products
    public LiveData<ProductServiceResponse> getMenProducts() {
        MutableLiveData<ProductServiceResponse> menProductsLiveData = new MutableLiveData<>();
        productService.getMenProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                menProductsLiveData.postValue(new ProductServiceResponse(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                menProductsLiveData.postValue(new ProductServiceResponse(t));
            }
        });

        return menProductsLiveData;
    }

    // Women Products
    public LiveData<ProductServiceResponse> getWomenProducts() {
        MutableLiveData<ProductServiceResponse> womenProductsLiveData = new MutableLiveData<>();
        productService.getWomenProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                womenProductsLiveData.postValue(new ProductServiceResponse(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                womenProductsLiveData.postValue(new ProductServiceResponse(t));
            }
        });

        return womenProductsLiveData;
    }

    // Footwear Products
    public LiveData<ProductServiceResponse> getFootwearProducts(){
        MutableLiveData<ProductServiceResponse> footwearProductsLiveData = new MutableLiveData<>();
        productService.getFootwearProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                footwearProductsLiveData.postValue(new ProductServiceResponse(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                footwearProductsLiveData.postValue(new ProductServiceResponse(t));
            }
        });
        return footwearProductsLiveData;
    }

    // Search Products
    public void searchProducts(String keyword){
        //searchedProductLiveData = new MutableLiveData<>();
        productService.searchProducts(keyword).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call,@NotNull Response<List<Product>> response) {
                searchedProductLiveData.postValue(new ProductServiceResponse(response.body()));
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                searchedProductLiveData.postValue(new ProductServiceResponse(t));
            }
        });
    }

    // get details
    public void getProductDetails(int id){
        productService.getProductDetails(id).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                productDetailsLiveData.postValue(
                        new ProductServiceResponse(Objects.requireNonNull(response.body()).get(0)));
                productStock = response.body() != null ? response.body().get(0).getStock() : 0;
                productPrice = response.body() != null ? response.body().get(0).getPrice() : 0;
                productName = response.body().get(0).getName();

            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
                productDetailsLiveData.postValue(new ProductServiceResponse(t));
            }
        });
    }

    public LiveData<ProductServiceResponse> getSearchedProducts(){
        return searchedProductLiveData;
    }

    public LiveData<ProductServiceResponse> getProductDetails(){
        return productDetailsLiveData;
    }

    public int getProductStock() {
        return productStock;
    }

    public int getProductPrice() {return productPrice;}


    public LiveData<Product> getWantedProductDetails(int id){
        MutableLiveData<Product> mutableLiveData = new MutableLiveData<>();
        productService.getProductDetails(id).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
                mutableLiveData.postValue(Objects.requireNonNull(response.body()).get(0));
                productStock = response.body() != null ? response.body().get(0).getStock() : 0;
                productPrice = response.body() != null ? response.body().get(0).getPrice() : 0;
                productName = response.body().get(0).getName();
            }

            @Override
            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
               // productDetailsLiveData.postValue(new ProductServiceResponse(t));
            }
        });
        return mutableLiveData;
    }

    public String getProductName() {
        return productName;
    }
}
