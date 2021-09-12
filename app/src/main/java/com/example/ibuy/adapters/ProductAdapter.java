package com.example.ibuy.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ibuy.R;
import com.example.ibuy.models.Product;
import com.example.ibuy.views.activities.ProductDetailActivity;
import com.squareup.picasso.Picasso;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent the adapter that will present the products
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private List<Product> productList = new ArrayList<>();

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_layout
                ,parent, false);
        return new ProductViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        // Renders product picture
        Picasso.get().load(currentProduct.getImage_url()).placeholder(R.drawable.product_placeholder)
                .into(holder.product_picture);
        // Product name/details
        holder.product_name.setText(currentProduct.getName());
        // Product price
        holder.product_price.setText(MessageFormat.format("${0}", currentProduct.getPrice()));
        // Product stock
        holder.product_stock.setText(MessageFormat.format("Stock: {0}", currentProduct.getStock()));
        // Send to new activity
        holder.product_view.setOnClickListener(v -> {
            Intent goToDetailActivity = new Intent(v.getContext(), ProductDetailActivity.class);
            goToDetailActivity.putExtra("product_id", currentProduct.getProduct_id());
            v.getContext().startActivity(goToDetailActivity);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setAllProducts(List<Product> productList){
        this.productList = productList;
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Fields for the product

        private final ImageView product_picture;
        private final TextView product_name;
        private final TextView product_price;
        private final TextView product_stock;
        private final RelativeLayout product_view;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            product_picture = itemView.findViewById(R.id.product_picture);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_stock = itemView.findViewById(R.id.product_stock);
            product_view = itemView.findViewById(R.id.product_view);
        }
    }
}
