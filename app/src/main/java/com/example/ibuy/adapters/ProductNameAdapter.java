package com.example.ibuy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ibuy.R;
import com.example.ibuy.models.Cart;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class ProductNameAdapter extends RecyclerView.Adapter<ProductNameAdapter.ProductViewHolder> {

    private final List<Cart> cartList;

    public ProductNameAdapter(List<Cart> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_name, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.productName.setText(cart.getName());
//        holder.productQty.setText(String.format(Locale.getDefault(),
//                "%d", cart.getQuantity()));
        holder.productQty.setText(MessageFormat.format("{0}pcs", cart.getQuantity()));
        holder.productPrice.setText(MessageFormat.format("$ {0}",
                cart.getPrice() * cart.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final TextView productQty;
        private final TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productQty = itemView.findViewById(R.id.product_qty);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
