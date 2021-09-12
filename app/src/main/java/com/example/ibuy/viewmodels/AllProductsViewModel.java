package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class AllProductsViewModel extends ViewModel {
    private final MediatorLiveData<ProductServiceResponse> mProductResponse;
    private final ProductRepository mProductRepository;

    public AllProductsViewModel() {
        mProductResponse = new MediatorLiveData<>();
        mProductRepository = new ProductRepository();
    }

    public LiveData<ProductServiceResponse> getAllProductsLiveData() {
        mProductResponse.addSource(mProductRepository.getAllProducts(), mProductResponse::setValue);
        return mProductResponse;
    }
}
