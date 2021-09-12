package com.example.ibuy.views.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.ibuy.R;
import com.example.ibuy.adapters.CartAdapter;
import com.example.ibuy.models.Cart;
import com.example.ibuy.models.Product;
import com.example.ibuy.viewmodels.ProductDetailsViewModel;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.example.ibuy.views.activities.CheckOutActivity;
import com.example.ibuy.views.activities.ProductDetailActivity;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private CartAdapter cartAdapter;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.card_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        TextView totalPrice = view.findViewById(R.id.total_price);
        cartAdapter = new CartAdapter(totalPrice);
        List<Product> productList = new ArrayList<>();
        UserManagerModelView userManagerModelView = new ViewModelProvider(this)
                .get(UserManagerModelView.class);
        userManagerModelView.getProgressBar().observe(getViewLifecycleOwner(), aBoolean -> {
            if(!aBoolean){
                progressBar.setVisibility(View.GONE);
            }
        });
        ProductDetailsViewModel productDetailsViewModel = new ViewModelProvider(this)
                .get(ProductDetailsViewModel.class);
        userManagerModelView.getCartProducts().observe(getViewLifecycleOwner(), cart -> {
            for (Cart c : cart) {
                productDetailsViewModel.getListOfProducts(c.getProduct_id()).observe(getViewLifecycleOwner(),
                        product -> {
                            product.setOriginal_price(product.getPrice());
                            product.setCart_qty(c.getQuantity());
                            product.setPrice(c.getQuantity() * product.getPrice());
                            productList.add(product);
                            cartAdapter.setAllCartProduct(productList);
                        });
            }
        });

        cartAdapter.setOnItemClickListener(new CartAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //send to detailsActivity
                Product product = productList.get(position);
                Intent intent = new Intent(requireActivity(), ProductDetailActivity.class);
                intent.putExtra(getString(R.string.selected_product_info), product.getProduct_id());
                requireActivity().startActivity(intent);
            }

            @Override
            public void onItemIncrease(int position) {
                //add to cart logic
                Product p = productList.get(position);
                userManagerModelView.addToCart(p.getProduct_id(), p.getStock(),p.getOriginal_price(), p.getName());
                userManagerModelView.getProductQty(p.getProduct_id()).observe(getViewLifecycleOwner(),
                        integer -> {
                            if (integer < p.getStock()) {
                                p.setCart_qty(integer + 1);
                                p.setPrice((p.getOriginal_price()) * (integer + 1));
                                cartAdapter.notifyItemChanged(position);
                                cartAdapter.notifyDataSetChanged();
                            }
                        });
            }

            @Override
            public void onItemDecrease(int position) {
                //remove from cart
                Product p = productList.get(position);
                userManagerModelView.removeFromCart(p.getProduct_id());
                userManagerModelView.getProductQty(p.getProduct_id()).observe(getViewLifecycleOwner(),
                        integer -> {
                            if (integer > 1) {
                                p.setCart_qty(integer - 1);
                                p.setPrice(p.getOriginal_price() * (integer - 1));
                                cartAdapter.notifyItemChanged(position);
                            } else {
                                productList.remove(position);
                                cartAdapter.notifyItemRemoved(position);
                            }
                            cartAdapter.notifyDataSetChanged();
                        });
            }

            @Override
            public void onLongClickRegistered(int position) {
                Product p = productList.get(position);
                showDialog(p, productList, position);
                cartAdapter.notifyItemRemoved(position);
                cartAdapter.notifyDataSetChanged();
            }

            private void showDialog(Product p, List<Product> products, int position) {
                new AlertDialog.Builder(getContext())
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle(R.string.prod_del)
                        .setMessage(R.string.del_warning)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            userManagerModelView.deleteFromCart(p.getProduct_id());
                            products.remove(position);
                            cartAdapter.notifyItemRemoved(position);
                            cartAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        recyclerView.setAdapter(cartAdapter);

        MaterialButton checkout = view.findViewById(R.id.checkout_button);
        checkout.setOnClickListener(v -> {
            Intent goToCheckOut = new Intent(requireActivity(), CheckOutActivity.class);
            goToCheckOut.putExtra(getString(R.string.from), getString(R.string.fragment));
            requireActivity().startActivity(goToCheckOut);
        });
        return view;
    }
}