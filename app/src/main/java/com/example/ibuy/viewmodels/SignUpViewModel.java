package com.example.ibuy.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ibuy.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class SignUpViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userMutableLiveData = userRepository.getUserMutableLiveData();
    }

    public void registerUser(String firstName, String lastName, String address, String email,
                             String password){
        userRepository.registerUser(firstName, lastName, address, email, password);
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
