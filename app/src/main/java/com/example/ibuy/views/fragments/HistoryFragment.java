package com.example.ibuy.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.ibuy.R;
import com.example.ibuy.adapters.BulkOrderAdapter;
import com.example.ibuy.adapters.OrderAdapter;
import com.example.ibuy.models.Order;
import com.example.ibuy.viewmodels.ProductDetailsViewModel;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.example.ibuy.views.activities.ReceiptActivity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private OrderAdapter orderAdapter;
    private BulkOrderAdapter bulkOrderAdapter;
    private final List<Order> orderList = new ArrayList<>();
    private final List<Order> bulkOrderList = new ArrayList<>();

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderAdapter = new OrderAdapter();
        UserManagerModelView userManagerModelView = new
                ViewModelProvider(this).get(UserManagerModelView.class);
        ProductDetailsViewModel productDetailsViewModel = new
                ViewModelProvider(this).get(ProductDetailsViewModel.class);
        userManagerModelView.getSingleProductOrders();
        userManagerModelView.getOrder().observe(this, order -> {
            for (Order o : order) {
                productDetailsViewModel.getListOfProducts(o.getProduct()).observe(getViewLifecycleOwner(),
                        product -> {
                            o.setName(product.getName());
                            orderList.add(o);
                            orderAdapter.setOrderList(orderList);
                        });
            }
        });

        userManagerModelView.getCartProductsOrdered().observe(this, orders -> {
            for (Order order : orders) {
                userManagerModelView.getOrderProducts(order.getOrder_id())
                        .observe(getViewLifecycleOwner(), cartList -> {
                            order.setCartList(cartList);
                            bulkOrderList.add(order);
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                bulkOrderAdapter.setOrderList(orders);
                                bulkOrderAdapter.notifyDataSetChanged();
                            }, 500);
                        });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bulkOrderAdapter = new BulkOrderAdapter(requireContext());
        RecyclerView singleOrdersRecyclerView = view.findViewById(R.id.orders);
        singleOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        singleOrdersRecyclerView.setHasFixedSize(true);
        singleOrdersRecyclerView.setAdapter(orderAdapter);

        orderAdapter.setOnClickListener(position -> {
            Order o = orderList.get(position);
            // allow user to see the order and download the receipt
            Intent intent = new Intent(requireContext(), ReceiptActivity.class);
            intent.putExtra(getString(R.string.from), getString(R.string.fragment));
            intent.putExtra(getString(R.string.type), getString(R.string.single_buy));
            intent.putExtra(getString(R.string.order_id), o.getOrder_id());
            intent.putExtra(getString(R.string.created_date), o.getCreated_date());
            intent.putExtra(getString(R.string.address).toLowerCase(), o.getAddress());
            intent.putExtra(getString(R.string.status), o.getStatus());
            intent.putExtra(getString(R.string.name), o.getName());
            intent.putExtra(getString(R.string.price), o.getPrice());
            requireActivity().startActivity(intent);
        });

        RecyclerView bulkOrdersRecyclerView = view.findViewById(R.id.bulk_orders);
        bulkOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        bulkOrdersRecyclerView.setHasFixedSize(true);
        bulkOrdersRecyclerView.setAdapter(bulkOrderAdapter);

        bulkOrderAdapter.setOnItemClickListener(position -> {
            Order o = bulkOrderList.get(position);
            Intent intent = new Intent(requireContext(), ReceiptActivity.class);
            intent.putExtra(getString(R.string.from), getString(R.string.fragment));
            intent.putExtra(getString(R.string.type), getString(R.string.bulk));
            intent.putExtra(getString(R.string.order_id), o.getOrder_id());
            intent.putExtra(getString(R.string.created_date), o.getCreated_date());
            intent.putExtra(getString(R.string.address).toLowerCase(), o.getAddress());
            intent.putExtra(getString(R.string.status), o.getStatus());
            intent.putExtra(getString(R.string.price), o.getPrice());
            intent.putExtra(getString(R.string.products_list), (Serializable) o.getCartList());
            requireActivity().startActivity(intent);
        });
    }
}