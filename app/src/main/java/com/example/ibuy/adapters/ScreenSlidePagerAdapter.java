package com.example.ibuy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ibuy.views.fragments.AllProductsFragment;
import com.example.ibuy.views.fragments.FashionFragment;
import com.example.ibuy.views.fragments.FootwearFragment;
import com.example.ibuy.views.fragments.MenFragment;
import com.example.ibuy.views.fragments.WomenFragment;

/**
 * Represent the adapter that will renders the different fragments
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
    public ScreenSlidePagerAdapter(FragmentActivity fa) {
        super(fa);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new AllProductsFragment();
            case 1:
                return new FashionFragment();
            case 2:
                return new MenFragment();
            case 3:
                return new WomenFragment();
            case 4:
                return new FootwearFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}
