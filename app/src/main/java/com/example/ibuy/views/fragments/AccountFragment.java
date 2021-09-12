package com.example.ibuy.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ibuy.R;
import com.example.ibuy.utils.Utils;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.example.ibuy.views.activities.LoginActivity;
import com.example.ibuy.views.activities.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private CircleImageView mProfilePicture;
    private TextView mName;
    private TextView mEmail;
    private TextView mShippingAddress;
    private UserManagerModelView mUserManagerModelView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserManagerModelView = new ViewModelProvider(this).get(UserManagerModelView.class);
        getUser();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProfilePicture = view.findViewById(R.id.profile_picture);
        mName = view.findViewById(R.id.name);
        mEmail = view.findViewById(R.id.email);
        mShippingAddress = view.findViewById(R.id.shipping_address);
        MaterialButton signOutButton = view.findViewById(R.id.sign_out_button);

        getUserInfo();

        signOutButton.setOnClickListener(v -> signOut() );
    }

    private void signOut() {
        mUserManagerModelView.signOut();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        requireActivity().startActivity(intent);
    }

    private void getUserInfo() {
        mUserManagerModelView.getUserInfoLiveData().observe(getViewLifecycleOwner(), user -> {
            Picasso.get().load(user.getProfile_pic()).placeholder(R.drawable.ic_avatar)
                    .into(mProfilePicture);
            String firstName = Utils.decrypt(user.getFirst_name());
            String lastName = Utils.decrypt(user.getLast_name());
            mName.setText(MessageFormat.format("{0} {1}", firstName, lastName));
            mEmail.setText(Utils.decrypt(user.getEmail()));
            mShippingAddress.setText(Utils.decrypt(user.getAddress()));
        });
    }

    private void getUser(){
        if(mUserManagerModelView.getSignedInUser() == null){
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            requireActivity().startActivity(intent);
            requireActivity().onBackPressed();
        }
    }

}