package com.example.ibuy.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ibuy.R;
import com.example.ibuy.models.Product;
import com.example.ibuy.viewmodels.ProductDetailsViewModel;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;
import java.text.MessageFormat;
import java.util.Objects;

/** Presents the details of a clicked product using the Product Service{details}, also giving the opportunity to Add to cart & Order
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */
public class ProductDetailActivity extends AppCompatActivity {


    // Views for the product
    private ImageView productPicture;
    private TextView productName;
    private TextView productCategories;
    private TextView productStock;
    private TextView productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        this.setContentView(R.layout.activity_product_detail);
        // Declaring views
        productPicture = findViewById(R.id.product_picture);
        productName = findViewById(R.id.product_name);
        productCategories = findViewById(R.id.product_categories);
        productStock = findViewById(R.id.product_stock);
        productPrice = findViewById(R.id.product_price);

        // Buttons allowing to addToCart() and buyNow()
        MaterialButton addToCartButton = findViewById(R.id.add_to_cart_button);
        MaterialButton checkOut = findViewById(R.id.checkout_button);


        // Getting the product item clicked
        int productId = getIntent().getIntExtra(getString(R.string.selected_product_info),0);

        // Assigning the view model and observing the result
        ProductDetailsViewModel productDetailsViewModel = new ViewModelProvider(this)
                .get(ProductDetailsViewModel.class);
        productDetailsViewModel.getProductDetails(productId);

        UserManagerModelView userManagerModelView = new ViewModelProvider(this)
                .get(UserManagerModelView.class);

        productDetailsViewModel.getProductDetailsLiveData().observe(this,
                productServiceResponse -> {

            if(productServiceResponse.getError() == null){
                Product product = productServiceResponse.getProduct();
                Picasso.get().load(product.getImage_url())
                    .placeholder(R.drawable.product_placeholder).into(productPicture);
            productName.setText(product.getName());
            productCategories.setText(product.getCategories());
            productPrice.setText(MessageFormat.format("$ {0}", product.getPrice()));
            productStock.setText(MessageFormat.format("Stock: {0}", product.getStock()));
            }else{
                Throwable e = productServiceResponse.getError();
                Toast.makeText(this, getString(R.string.error_msg) +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        addToCartButton.setOnClickListener(v ->{
                // check if user has account
                if(userManagerModelView.getSignedInUser() != null){
                    userManagerModelView.addToCart(productId, productDetailsViewModel.getStock()
                            ,productDetailsViewModel.getPrice(),productDetailsViewModel.getName());
                } else{
                    // Send to Login fragment
                   Intent intent = new Intent(this, LoginActivity.class);
                   startActivity(intent);
                   finish();
                }
        });

        checkOut.setOnClickListener(v->{
            if(userManagerModelView.getSignedInUser() != null) {
                Intent goToCheckOutIntent = new Intent(this, CheckOutActivity.class);
                goToCheckOutIntent.putExtra(getString(R.string.selected_product_info), productId);
                goToCheckOutIntent.putExtra(getString(R.string.from), getString(R.string.activity));
                startActivity(goToCheckOutIntent);
            }else{
                // Send to Login Fragment
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
