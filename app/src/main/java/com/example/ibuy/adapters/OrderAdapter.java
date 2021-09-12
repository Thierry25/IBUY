package com.example.ibuy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ibuy.R;
import com.example.ibuy.models.Order;
import com.example.ibuy.utils.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private onClickListener onClickListener;
    private List<Order> orderList = new ArrayList<>();

    public void setOnClickListener(onClickListener listener) {
        onClickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_layout, parent, false);
        return new OrderViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order currentOrder = orderList.get(position);
        holder.orderId.setText(currentOrder.getOrder_id());
        holder.date.setText(Utils.getDate(currentOrder.getCreated_date()));
        holder.productName.setText(currentOrder.getName());
        holder.productPrice.setText(MessageFormat.format("${0}", currentOrder.getPrice()));
        holder.shippingAddress.setText(currentOrder.getAddress());
        holder.price.setText(MessageFormat.format("${0}", currentOrder.getPrice()));
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private final TextView orderId;
        private final TextView date;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView shippingAddress;
        private final TextView price;


        public OrderViewHolder(@NonNull View itemView, onClickListener listener) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            date = itemView.findViewById(R.id.date);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            shippingAddress = itemView.findViewById(R.id.shipping_address);
            price = itemView.findViewById(R.id.price);

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

    public interface onClickListener {
        void onItemClick(int position);
    }
}
