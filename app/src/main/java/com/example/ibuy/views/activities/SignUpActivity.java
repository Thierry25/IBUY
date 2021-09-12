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
import com.example.ibuy.viewmodels.SignUpViewModel;
import com.google.android.material.button.MaterialButton;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel mSignUpViewModel;
    // ids of EditText
    private final int[] editTextIds = new int[]
            {
                    R.id.first_name,
                    R.id.last_name,
                    R.id.address,
                    R.id.email,
                    R.id.password,
                    R.id.confirm_password
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.fragment_sign_up);
        mSignUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        // Observing the value of the list from the view model
        mSignUpViewModel.getUserMutableLiveData().observe(this, firebaseUser -> {
            if(firebaseUser != null){
                Toast.makeText(this, R.string.successful_registration, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });// Views declaration
        EditText firstName = findViewById(editTextIds[0]);
        EditText lastName = findViewById(editTextIds[1]);
        EditText address = findViewById(editTextIds[2]);
        EditText email = findViewById(editTextIds[3]);
        EditText password = findViewById(editTextIds[4]);
        EditText confirmPassword = findViewById(editTextIds[5]);
        MaterialButton signUpButton = findViewById(R.id.sign_up_button);
        LinearLayout alreadyHaveAccount = findViewById(R.id.have_account_layout);
        //        LinearLayout darkBg = findViewById(R.id.dark_bg);
//        ProgressBar progressBar = findViewById(R.id.progress_bar);

        signUpButton.setOnClickListener(v->{
            if(!validateEditText(editTextIds)){
                String passwordEntered = password.getText().toString().trim();
                String confirmPasswordEntered = confirmPassword.getText().toString().trim();
                if(passwordEntered.equals(confirmPasswordEntered)){
                    String firstNameEntered = firstName.getText().toString().trim();
                    String lastNameEntered = lastName.getText().toString().trim();
                    String addressEntered = address.getText().toString().trim();
                    String emailEntered = email.getText().toString().trim();
                    registerUser(firstNameEntered, lastNameEntered, addressEntered, emailEntered,
                            passwordEntered);
                }else{
                    Toast.makeText(this, R.string.password_error,
                            Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, R.string.fill_info, Toast.LENGTH_SHORT).show();
            }
        });
        alreadyHaveAccount.setOnClickListener(v-> finish());
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

    private void registerUser(String firstName, String lastName, String address,
                              String email, String password){
        mSignUpViewModel.registerUser(firstName, lastName, address, email, password);
    }


}