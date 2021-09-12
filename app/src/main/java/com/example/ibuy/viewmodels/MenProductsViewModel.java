package com.example.ibuy.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ibuy.models.ProductServiceResponse;
import com.example.ibuy.repositories.ProductRepository;

public class MenProductsViewModel extends ViewModel {
    private final MediatorLiveData<ProductServiceResponse> mProductsResponse;
    private final ProductRepository mProductRepository;

    public MenProductsViewModel(){
        mProductsResponse = new MediatorLiveData<>();
        mProductRepository = new ProductRepository();
    }

    public LiveData<ProductServiceResponse> getMenProductsLiveData(){
        mProductsResponse.addSource(mProductRepository.getMenProducts(),mProductsResponse::setValue);
        return mProductsResponse;
    }

}
