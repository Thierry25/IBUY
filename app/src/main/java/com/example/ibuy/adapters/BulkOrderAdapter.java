package com.example.ibuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ibuy.R;
import com.example.ibuy.models.Cart;
import com.example.ibuy.models.Order;
import com.example.ibuy.utils.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class BulkOrderAdapter extends RecyclerView.Adapter<BulkOrderAdapter.BulkOrderViewHolder> {

    private List<Order> orderList = new ArrayList<>();
    private onItemClickListener onItemClickListener;

    private final Context context;

    public BulkOrderAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(onItemClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public BulkOrderAdapter.BulkOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bulk_order_layout, parent, false);
        return new BulkOrderViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BulkOrderAdapter.BulkOrderViewHolder holder, int position) {
        Order currentOrder = orderList.get(position);

        holder.orderId.setText(currentOrder.getOrder_id());
        holder.date.setText(Utils.getDate(currentOrder.getCreated_date()));
        holder.shippingAddress.setText(currentOrder.getAddress());
        holder.price.setText(MessageFormat.format("${0}", currentOrder.getPrice()));
        holder.productName.setLayoutManager(new LinearLayoutManager(context));
        holder.productName.setHasFixedSize(true);
        setProductNameRecycler(holder.productName, currentOrder.getCartList());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrderList(List<Order> list) {
        this.orderList = list;
        notifyDataSetChanged();
    }

    public static class BulkOrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderId;
        private final TextView date;
        private final TextView shippingAddress;
        private final TextView price;
        private final RecyclerView productName;


        public BulkOrderViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            date = itemView.findViewById(R.id.date);
            shippingAddress = itemView.findViewById(R.id.shipping_address);
            price = itemView.findViewById(R.id.price);
            productName = itemView.findViewById(R.id.product_name);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public void setProductNameRecycler(RecyclerView recyclerView, List<Cart> cartList){
        ProductNameAdapter productNameAdapter = new ProductNameAdapter(cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(productNameAdapter);
    }
}
