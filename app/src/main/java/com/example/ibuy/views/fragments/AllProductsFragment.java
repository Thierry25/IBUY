package com.example.ibuy.views.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ibuy.R;
import com.example.ibuy.adapters.ProductAdapter;
import com.example.ibuy.viewmodels.AllProductsViewModel;

/** Presents all the products from the Product Service
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class AllProductsFragment extends Fragment {

    private ProductAdapter mProductAdapter;

    public AllProductsFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductAdapter = new ProductAdapter();
        AllProductsViewModel viewModel = new ViewModelProvider(this)
                .get(AllProductsViewModel.class);
        viewModel.getAllProductsLiveData().observe(this, productServiceResponse -> {
            if (productServiceResponse.getError() == null) {
                mProductAdapter.setAllProducts(productServiceResponse.getProducts());
            } else {
                Throwable e = productServiceResponse.getError();
                Toast.makeText(getContext(), getString(R.string.error_msg) +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_all_products, container, false);

        RecyclerView allProductsRecyclerView = root.findViewById(R.id.all_products_list);
        allProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allProductsRecyclerView.setAdapter(mProductAdapter);
        return root;
    }
}