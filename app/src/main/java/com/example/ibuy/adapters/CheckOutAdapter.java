package com.example.ibuy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ibuy.R;
import com.example.ibuy.models.Product;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.CheckViewHolder>{

    private List<Product> checkOutProductList = new ArrayList<>();
    private final TextView textView;

    public CheckOutAdapter(TextView textView) {
        this.textView = textView;
    }

    @NonNull
    @Override
    public CheckOutAdapter.CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.checkout_product, parent, false);
        return new CheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckOutAdapter.CheckViewHolder holder, int position) {
        Product currentProduct = checkOutProductList.get(position);
        Picasso.get().load(currentProduct.getImage_url()).placeholder(R.drawable.product_placeholder)
                .into(holder.productPicture);
        holder.productName.setText(currentProduct.getName());
        holder.productPrice.setText(MessageFormat.format("$ {0}", currentProduct.getPrice()));

        if(position == checkOutProductList.size() - 1){
            textView.setText(String.valueOf(getMyTotalQuantity()));
        }
    }

    @Override
    public int getItemCount() {
        return checkOutProductList.size();
    }

    public void setCheckOutProductList(List<Product> allCartProduct) {
        this.checkOutProductList = allCartProduct;
        notifyDataSetChanged();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productPicture;
        private final TextView productName;
        private final TextView productPrice;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
            productPicture = itemView.findViewById(R.id.product_picture);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }

    public int getMyTotalQuantity() {
        int totalPrice = 0;
        for (int i = 0; i < checkOutProductList.size(); i++) {
            if (checkOutProductList.get(i) != null) {
                totalPrice = totalPrice + checkOutProductList.get(i).getPrice();
            }
        }
        return totalPrice;
    }
}
