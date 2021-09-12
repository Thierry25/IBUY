package com.example.ibuy.views.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibuy.R;
import com.example.ibuy.adapters.CheckOutAdapter;
import com.example.ibuy.models.Cart;
import com.example.ibuy.models.Product;
import com.example.ibuy.utils.PayPalConfig;
import com.example.ibuy.utils.Utils;
import com.example.ibuy.viewmodels.ProductDetailsViewModel;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.google.android.material.button.MaterialButton;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static net.ticherhaz.tarikhmasa.TarikhMasa.ConvertTarikhMasa2LocalTime;
import static net.ticherhaz.tarikhmasa.TarikhMasa.GetTarikhMasa;

public class CheckOutActivity extends AppCompatActivity {

    private static final PayPalConfiguration configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);

    private TextView mPrice;
    private ImageView mProductPicture;
    private TextView mProductName;
    private CircleImageView mProfilePicture;
    private TextView mName;
    private TextView mEmail;
    private TextView mShippingAddress;
    private TextView mDateTime;
    private CheckOutAdapter mCartAdapter;
    private final List<Product> mProductList = new ArrayList<>();
    private UserManagerModelView mUserManagerModelView;
    private ProductDetailsViewModel mProductDetailsViewModel;
    private int mProductId;
    private String from;


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_check_out);
        startPayPalService();

        // Declaring the views
        mPrice = findViewById(R.id.price);
        RecyclerView mProductList = findViewById(R.id.product_list);
        mProductList.setLayoutManager(new LinearLayoutManager(this));
        mProductList.setHasFixedSize(true);
        RelativeLayout mSingleProductLayout = findViewById(R.id.single_product_view);
        mProductPicture = findViewById(R.id.product_picture);
        mProductName = findViewById(R.id.product_name);

        mProfilePicture = findViewById(R.id.profile_picture);
        mName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);
        mShippingAddress = findViewById(R.id.shipping_address);
        mDateTime = findViewById(R.id.date_time);
        MaterialButton orderButton = findViewById(R.id.order);
        mCartAdapter = new CheckOutAdapter(mPrice);
        mProductList.setAdapter(mCartAdapter);

        mUserManagerModelView = new ViewModelProvider(this)
                .get(UserManagerModelView.class);
        mProductDetailsViewModel = new ViewModelProvider(this)
                .get(ProductDetailsViewModel.class);
        // Getting the user information
        getUserInfo();

        // Verify where the intent is coming from

        from = getIntent().getStringExtra(getString(R.string.from));
        if (from.equals(getString(R.string.activity))) {
            // recyclerview gone, relativelayout visible
            mSingleProductLayout.setVisibility(View.VISIBLE);
            mProductId = getIntent().getIntExtra(getString(R.string.selected_product_info), 0);
            mProductList.setVisibility(View.GONE);
            getProductDetails(mProductId);
        } else {
            mSingleProductLayout.setVisibility(View.GONE);
            mProductList.setVisibility(View.VISIBLE);
            getCartInfo();
        }
        orderButton.setOnClickListener(v -> processPayment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayPalConfig.PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = Objects.requireNonNull(data)
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        if (from.equals(getString(R.string.activity))) {
                            String paymentDetails = confirmation.toJSONObject().toString(4);
                            startActivity(new Intent(this, OrderActivity.class)
                                    .putExtra(getString(R.string.payment_details), paymentDetails)
                                    .putExtra(getString(R.string.payment_amount), mPrice.getText().toString())
                                    .putExtra(getString(R.string.selected_product_info), mProductId)
                                    .putExtra(getString(R.string.from), getString(R.string.activity))
                                    .putExtra(getString(R.string.address).toLowerCase(),
                                            mShippingAddress.getText().toString()));
                        } else {
                            String paymentDetails = confirmation.toJSONObject().toString(4);
                            startActivity(new Intent(this, OrderActivity.class)
                                    .putExtra(getString(R.string.payment_details), paymentDetails)
                                    .putExtra(getString(R.string.payment_amount), mPrice.getText().toString())
                                    .putExtra(getString(R.string.from), getString(R.string.fragment))
                                    .putExtra(getString(R.string.address).toLowerCase(),
                                            mShippingAddress.getText().toString()));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, R.string.cancelled, Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
        }
    }

    private void getProductDetails(int productId) {
        ProductDetailsViewModel productDetailsViewModel = new ViewModelProvider(this)
                .get(ProductDetailsViewModel.class);
        productDetailsViewModel.getProductDetails(productId);
        productDetailsViewModel.getProductDetailsLiveData().observe(this,
                productServiceResponse -> {
                    if (productServiceResponse.getError() == null) {
                        Product product = productServiceResponse.getProduct();
                        Picasso.get().load(product.getImage_url())
                                .placeholder(R.drawable.product_placeholder).into(mProductPicture);
                        mProductName.setText(product.getName());
                        mPrice.setText(String.valueOf(product.getPrice()));
                    }
                });
    }

    private void getUserInfo() {
        UserManagerModelView userManagerModelView = new ViewModelProvider(this)
                .get(UserManagerModelView.class);
        userManagerModelView.getUserInfoLiveData().observe(this, user -> {
            Picasso.get().load(user.getProfile_pic()).placeholder(R.drawable.ic_avatar)
                    .into(mProfilePicture);
            String firstName = Utils.decrypt(user.getFirst_name());
            String lastName = Utils.decrypt(user.getLast_name());
            mName.setText(MessageFormat.format("{0} {1}", firstName, lastName));
            mEmail.setText(Utils.decrypt(user.getEmail()));
            mShippingAddress.setText(Utils.decrypt(user.getAddress()));
            mDateTime.setText(ConvertTarikhMasa2LocalTime(GetTarikhMasa()));
        });
    }

    private void startPayPalService() {
        // Setting PayPal Service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        startService(intent);
    }

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(mPrice.getText().toString()),
                getString(R.string.currency), getString(R.string.payment_confirmation),
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PayPalConfig.PAYPAL_REQUEST_CODE);

    }

    private void getCartInfo() {
        mUserManagerModelView.getCartProducts().observe(this, cart -> {
            for (Cart c : cart) {
                mProductDetailsViewModel.getListOfProducts(c.getProduct_id()).observe(this,
                        product -> {
                            product.setOriginal_price(product.getPrice());
                            product.setCart_qty(c.getQuantity());
                            product.setPrice(c.getQuantity() * product.getPrice());
                            mProductList.add(product);
                            mCartAdapter.setCheckOutProductList(mProductList);
                        });
            }
        });
    }
}