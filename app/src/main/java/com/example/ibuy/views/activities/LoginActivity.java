package com.example.ibuy.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.ibuy.R;
import com.example.ibuy.viewmodels.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel mLoginViewModel;
    private final int[] editTextIds = {R.id.username_entered, R.id.password_entered};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.fragment_login);
        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mLoginViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
            if(firebaseUser != null){
                Toast.makeText(this, getString(R.string.login_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        MaterialButton mSingInButton = findViewById(R.id.sign_in_button);
        EditText mUsernameEntered = findViewById(editTextIds[0]);
        EditText mPasswordEntered = findViewById(editTextIds[1]);
        LinearLayout mNoAccountLayout = findViewById(R.id.no_account_layout);

        mSingInButton.setOnClickListener(v-> {
            String username = mUsernameEntered.getText().toString().trim();
            String password = mPasswordEntered.getText().toString().trim();
            if(!validateEditText(editTextIds)){
                loginUser(username, password);
            }
        });

        mNoAccountLayout.setOnClickListener(v ->{
            // send to sign up
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    public void loginUser(String email, String password){
        mLoginViewModel.login(email, password);
    }

    private boolean validateEditText(int[] ids) {
        boolean isEmpty = false;

        for(int id: ids){
            EditText editText = findViewById(id);
            if(TextUtils.isEmpty(editText.getText().toString().trim())) {
                editText.setError(getString(R.string.empty_edit_text));
                isEmpty = true;
            }
        }
        return isEmpty;
    }
}