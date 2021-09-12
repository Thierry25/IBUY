package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ibuy.models.Product;
import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class ProductDetailsViewModel extends ViewModel {
    private final LiveData<ProductServiceResponse> mProductsResponse;
    private final ProductRepository mProductsRepository;

    public ProductDetailsViewModel(){
        mProductsRepository = new ProductRepository();
        mProductsResponse = mProductsRepository.getProductDetails();
    }

    public void getProductDetails(int id){
        mProductsRepository.getProductDetails(id);
    }

    public LiveData<ProductServiceResponse> getProductDetailsLiveData(){
        return mProductsResponse;
    }

    public int getStock(){
        return mProductsRepository.getProductStock();
    }

    public int getPrice(){
        return mProductsRepository.getProductPrice();
    }

    public LiveData<Product> getListOfProducts(int id){
        return mProductsRepository.getWantedProductDetails(id);
    }

    public String getName() {return mProductsRepository.getProductName();}

}
