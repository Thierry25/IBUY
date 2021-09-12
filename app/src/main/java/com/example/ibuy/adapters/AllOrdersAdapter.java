package com.example.ibuy.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ibuy.R;
import com.example.ibuy.models.Order;

import java.util.ArrayList;
import java.util.List;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.AllOrdersViewHolder> {
    private List<Order> orderList = new ArrayList<>();

    private onItemClickListener onItemClickListener;

    public void setOnItemClickListener(onItemClickListener listener){
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AllOrdersAdapter.AllOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_orders_layout, parent, false);
        return new AllOrdersViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AllOrdersAdapter.AllOrdersViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderId.setText(order.getOrder_id());
        holder.userId.setText(order.getUser_id());
        Log.i("JESUS", order.getOrder_id());
        Log.i("JESUS1", order.getUser_id());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setAllOrdersList(List<Order> allOrdersList){
        this.orderList = allOrdersList;
        notifyDataSetChanged();
    }

    public static class AllOrdersViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderId;
        private final TextView userId;


        public AllOrdersViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);

            orderId = itemView.findViewById(R.id.order_id);
            userId = itemView.findViewById(R.id.user_id);

            if(listener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }
}
