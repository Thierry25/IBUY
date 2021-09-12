package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class WomenProductsViewModel extends ViewModel {
    private final MediatorLiveData<ProductServiceResponse> mProductsResponse;
    private final ProductRepository mProductsRepository;

    public WomenProductsViewModel(){
        mProductsResponse = new MediatorLiveData<>();
        mProductsRepository = new ProductRepository();
    }

    public LiveData<ProductServiceResponse> getWomenProductsLiveData(){
        mProductsResponse.addSource(mProductsRepository.getWomenProducts(), mProductsResponse::setValue);
        return mProductsResponse;
    }


}
