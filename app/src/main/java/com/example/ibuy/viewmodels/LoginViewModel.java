package com.example.ibuy.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ibuy.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userMutableLiveData = userRepository.getUserMutableLiveData();
    }

    public void login(String username, String password){
        userRepository.loginUser(username, password);
    }

   // public FirebaseUser getCurrentUser(){
    //   return userRepository.getSignedInUser();
    //}

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
