package com.example.ibuy.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ibuy.R;
import com.example.ibuy.viewmodels.UserManagerModelView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    private LinearLayout mBackgroundLayout;
    private ProgressBar mProgressBar;
    private UserManagerModelView mUserManagerModelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_try);

        Intent intent = getIntent();
        // declare views
        mBackgroundLayout = findViewById(R.id.bg_layout);
        mProgressBar = findViewById(R.id.progress_bar);
        TextView link = findViewById(R.id.link);
        link.setOnClickListener(v-> goHome());

        updateView();

        try {
            JSONObject jsonObject = new JSONObject(
                    Objects.requireNonNull(intent.getStringExtra(getString(R.string.payment_details))));
            showDetails(intent, jsonObject.getJSONObject(getString(R.string.response))
                    , intent.getStringExtra(getString(R.string.payment_amount)),
                    intent.getStringExtra(getString(R.string.address).toLowerCase()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetails(Intent intent, JSONObject response, String paymentAmount, String address) {
        try {
            String orderId = response.getString(getString(R.string.id));
            String status = response.getString(getString(R.string.state));
            int price = Integer.parseInt(paymentAmount);

            if(Objects.requireNonNull(intent.getStringExtra(getString(R.string.from))).equals(getString(R.string.activity))) {
                int productId = intent.getIntExtra(getString(R.string.selected_product_info), 0);
                if (response.getString(getString(R.string.state)).equals(getString(R.string.approved))) {
                    // create method in repository to upload the order placed.
                    mUserManagerModelView.orderProduct(orderId, productId, price, status, address);
                }
            }else{
                if(response.getString(getString(R.string.state)).equals(getString(R.string.approved))){
                    // bulk
                    mUserManagerModelView.getCartProducts().observe(this, carts ->
                            mUserManagerModelView.buyAllProductsFromCart(orderId, address, status,
                            price,carts));

                }else{
                    Toast.makeText(this, R.string.paypal_error, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        mUserManagerModelView = new ViewModelProvider(this).get(UserManagerModelView.class);
        mUserManagerModelView.getProgressBar().observe(this, aBoolean -> {
            if (!aBoolean) {
                mBackgroundLayout.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
       goHome();
    }

    public void goHome(){
        Intent intent = new Intent(OrderActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}