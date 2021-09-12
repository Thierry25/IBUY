package com.example.ibuy.views.fragments;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.example.ibuy.R;
import com.example.ibuy.adapters.CartPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.Objects;

public class CartFragment extends Fragment {

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        Toolbar toolbar = view.findViewById(R.id.main_toolbar_layout);
        TextView toolbarText = toolbar.findViewById(R.id.toolbar_tittle);
        ImageView imageView = toolbar.findViewById(R.id.menu_button);
        imageView.setVisibility(View.GONE);
        ImageView menuBack = toolbar.findViewById(R.id.menu_back);
        menuBack.setVisibility(View.VISIBLE);
        menuBack.setOnClickListener(v -> requireActivity().onBackPressed());
        toolbarText.setText(requireContext().getString(R.string.app_name));
        String[] mTitles = {
                getString(R.string.current_orders), getString(R.string.order_history)};

        CartPagerAdapter cartPagerAdapter = new CartPagerAdapter(requireActivity());
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = view.findViewById(R.id.view_pager);
        viewPager2.setCurrentItem(0);

        viewPager2.setAdapter(cartPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(mTitles[position])
        ).attach();
        return view;
    }
}