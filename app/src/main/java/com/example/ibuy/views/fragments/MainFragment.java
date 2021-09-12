package com.example.ibuy.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ibuy.R;
import com.example.ibuy.adapters.AllOrdersAdapter;
import com.example.ibuy.adapters.ProductAdapter;
import com.example.ibuy.adapters.ScreenSlidePagerAdapter;
import com.example.ibuy.models.Order;
import com.example.ibuy.utils.Utils;
import com.example.ibuy.viewmodels.SearchedProductsViewModel;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.example.ibuy.views.activities.LoginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Represents the main page of the application.
 * @author Thierry Marcelin & Loudwige Odice
 * @version 1.0
 * @since 2020-11-25
 */

public class MainFragment extends Fragment {

    private TabLayout mTabLayout;
    private EditText mSearchText;
    private RelativeLayout mSearchLayout;

    private ProductAdapter mProductAdapter;
    private RecyclerView mSearchedList;

    private TextView mErrorMessage;

    private final int[] mTabIcons = {
      R.drawable.ic_baseline_all_inclusive_24, R.drawable.ic_baseline_star_24,
            R.drawable.ic_baseline_person_24,R.drawable.ic_baseline_face_24,
            R.drawable.high_heel
    };
    private SearchedProductsViewModel model;
    private UserManagerModelView mUserManagerModelView;
    private TextView mWelcomeText;

    private RelativeLayout mTopViewModel;
    private LinearLayout mainPageContent;
    private RelativeLayout mAdminLayout;
    private AllOrdersAdapter allOrdersAdapter;
    private List<Order> ordersList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProductAdapter = new ProductAdapter();
        allOrdersAdapter = new AllOrdersAdapter();
        mUserManagerModelView = new ViewModelProvider(this).get(UserManagerModelView.class);
        model = new ViewModelProvider(this)
                .get(SearchedProductsViewModel.class);
        model.getSearchedProductsLiveData().observe(this, productServiceResponse -> {
            if(productServiceResponse.getError() == null){
                mProductAdapter.setAllProducts(productServiceResponse.getProducts());
                mErrorMessage.setVisibility(View.GONE);
                mSearchedList.setVisibility(View.VISIBLE);
            }else{
                Throwable e = productServiceResponse.getError();
                Toast.makeText(getContext(), getString(R.string.error_msg) +
                        e.getMessage(), Toast.LENGTH_SHORT).show();
                mErrorMessage.setVisibility(View.VISIBLE);
                mSearchedList.setVisibility(View.GONE);
            }
        });

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mWelcomeText = root.findViewById(R.id.welcome_text);
        mTopViewModel = root.findViewById(R.id.top_view_layout);
        mainPageContent = root.findViewById(R.id.main_page_content);
        mAdminLayout = root.findViewById(R.id.admin_layout);
        RecyclerView mAllOrdersRecyclerView = root.findViewById(R.id.all_orders);
        mAllOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAllOrdersRecyclerView.setHasFixedSize(true);
        mUserManagerModelView.getUserRole();

        mUserManagerModelView.getLiveData().observe(getViewLifecycleOwner(), s->{
            if(s.equals(getString(R.string.admin))){
                mTopViewModel.setVisibility(View.GONE);
                mainPageContent.setVisibility(View.GONE);
                mAdminLayout.setVisibility(View.VISIBLE);
                getAllOrders(mAllOrdersRecyclerView);
            }else{
                mTopViewModel.setVisibility(View.VISIBLE);
                mainPageContent.setVisibility(View.VISIBLE);
                mAdminLayout.setVisibility(View.GONE);
            }
        });
        mUserManagerModelView.getUserInfoLiveData().observe(getViewLifecycleOwner(), user ->
                mWelcomeText.setText(MessageFormat.format("{0}{1}",
                        getString(R.string.welcome_user), Utils.decrypt(user.getFirst_name()))));

        String[] mTitles = {
                getString(R.string.all_products), getString(R.string.fashion_products),
                getString(R.string.men_products), getString(R.string.women_products),
                getString(R.string.footwear_products)
        };
        ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(getActivity());
        ViewPager2 mViewPager = root.findViewById(R.id.view_pager);
        mViewPager.setCurrentItem(0);

        // Creating the tabs
        mTabLayout = root.findViewById(R.id.tab_layout);

        mViewPager.setAdapter(screenSlidePagerAdapter);

        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(mTitles[position])
        ).attach();

        setupTabIcons();

        //
        mSearchLayout = root.findViewById(R.id.search_relativeLayout);
        mSearchedList = root.findViewById(R.id.searched_list);
        mErrorMessage = root.findViewById(R.id.error_msg);
        mSearchedList.setLayoutManager(new LinearLayoutManager(getActivity()));

       //

        mSearchText = root.findViewById(R.id.search_text);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(mSearchText.getText().toString().trim())){
                    mTabLayout.setVisibility(View.VISIBLE);
                    mViewPager.setVisibility(View.VISIBLE);
                    mSearchLayout.setVisibility(View.GONE);
                }else{
                   mSearchLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String enteredString = editable.toString().trim().toLowerCase();
                if(!enteredString.equals("")) {
                    // Search for value
                    model.searchProducts(enteredString);
                }
            }
        });
        mSearchedList.setAdapter(mProductAdapter);

        allOrdersAdapter.setOnItemClickListener(position -> {
            Order o = ordersList.get(position);
//            Intent intent = new Intent(requireContext(), xx.clas);
//            intent
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.cart){
            // Check if user is signed in. If signed in, go To cart else ask to log in/ signUp
            if(mUserManagerModelView.getSignedInUser() != null){
                // Go to cart
                //MainFragment fragment = new MainFragment();
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_nav_home_to_cartFragment3);
            }else{
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                requireActivity().startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Method to setup icons to the tab layouts.
     */

    private void setupTabIcons() {
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setIcon(mTabIcons[0]);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setIcon(mTabIcons[1]);
        Objects.requireNonNull(mTabLayout.getTabAt(2)).setIcon(mTabIcons[2]);
        Objects.requireNonNull(mTabLayout.getTabAt(3)).setIcon(mTabIcons[3]);
        Objects.requireNonNull(mTabLayout.getTabAt(4)).setIcon(mTabIcons[4]);
    }

    private void getAllOrders(RecyclerView recyclerView){
        mUserManagerModelView.getAllOrdersLiveData().observe(getViewLifecycleOwner(), orders -> {
            allOrdersAdapter.setAllOrdersList(orders);
            ordersList = orders;
            recyclerView.setAdapter(allOrdersAdapter);
        });
    }


}