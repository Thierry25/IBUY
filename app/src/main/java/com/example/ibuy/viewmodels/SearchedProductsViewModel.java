package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class SearchedProductsViewModel extends ViewModel {
    private final MediatorLiveData<ProductServiceResponse> mProductsResponse;
    private final ProductRepository mProductRepository;

    public SearchedProductsViewModel(){
        mProductRepository = new ProductRepository();
        mProductsResponse = new MediatorLiveData<>();
    }

    public void searchProducts(String keyword){
        mProductRepository.searchProducts(keyword);
    }

    public LiveData<ProductServiceResponse> getSearchedProductsLiveData(){
        mProductsResponse.addSource(mProductRepository.getSearchedProducts(), mProductsResponse::setValue);
        return mProductsResponse;
    }
}
