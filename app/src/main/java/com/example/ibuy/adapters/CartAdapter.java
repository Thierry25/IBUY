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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartProductList = new ArrayList<>();
    private onItemClickListener mListener;
    private final TextView textView;

    public CartAdapter(TextView textView) {
        this.textView = textView;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_product_layout, parent, false);
        return new CartViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product currentProduct = cartProductList.get(position);
        Picasso.get().load(currentProduct.getImage_url()).placeholder(R.drawable.product_placeholder)
                .into(holder.productPicture);
        holder.productName.setText(currentProduct.getName());
        holder.productQty.setText(String.format(Locale.getDefault(),
                "%d", currentProduct.getCart_qty()));
        holder.productPrice.setText(MessageFormat.format("$ {0}", currentProduct.getPrice()));

        if(position == cartProductList.size() - 1){
            textView.setText(MessageFormat.format("$ {0}",
                    String.valueOf(getMyTotalQuantity())));
        }
    }

    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    public void setAllCartProduct(List<Product> allCartProduct) {
        this.cartProductList = allCartProduct;
        notifyDataSetChanged();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final ImageView productPicture;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView productQty;

        public CartViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);

            productPicture = itemView.findViewById(R.id.product_picture);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            ImageView decreaseProductQty = itemView.findViewById(R.id.decrease_product_qty);
            productQty = itemView.findViewById(R.id.product_qty);
            ImageView increaseProductQty = itemView.findViewById(R.id.increase_product_qty);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
            increaseProductQty.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemIncrease(position);
                    }
                }
            });
            decreaseProductQty.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemDecrease(position);
                    }
                }
            });

            itemView.setOnLongClickListener(v->{
                if(listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onLongClickRegistered(position);
                    }
                }

                return true;
            });
        }
    }

    public int getMyTotalQuantity() {
        int totalPrice = 0;
        for (int i = 0; i < cartProductList.size(); i++) {
            if (cartProductList.get(i) != null) {
                totalPrice = totalPrice + cartProductList.get(i).getPrice();
            }
        }
        return totalPrice;
    }

    public interface onItemClickListener {
        void onItemClick(int position);

        void onItemIncrease(int position);

        void onItemDecrease(int position);

        void onLongClickRegistered(int position);
    }
}
