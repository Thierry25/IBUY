package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class FashionProductsViewModel extends ViewModel {
    private final MediatorLiveData<ProductServiceResponse> mProductResponse;
    private final ProductRepository mProductRepository;

    public FashionProductsViewModel(){
        mProductRepository = new ProductRepository();
        mProductResponse = new MediatorLiveData<>();
    }

    public LiveData<ProductServiceResponse> getFashionProductsLiveData(){
        mProductResponse.addSource(mProductRepository.getFashionProducts(), mProductResponse::setValue);
        return mProductResponse;
    }
}
