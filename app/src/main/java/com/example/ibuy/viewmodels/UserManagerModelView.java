package com.example.ibuy.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ibuy.models.Cart;
import com.example.ibuy.models.Order;
import com.example.ibuy.models.User;
import com.example.ibuy.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserManagerModelView extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MediatorLiveData<User> userMediatorLiveData;
    private final LiveData<List<Order>> orderLiveData;
    private final LiveData<String> liveData;
    private final LiveData<List<Order>> allOrdersLiveData;

    public UserManagerModelView(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userMediatorLiveData = new MediatorLiveData<>();
        orderLiveData = userRepository.getOrderLiveData();
        liveData = userRepository.getLiveData();
        allOrdersLiveData = userRepository.getAllOrders();
    }

    public FirebaseUser getSignedInUser() {
        return userRepository.getSignedInUser();
    }

    public LiveData<User> getUserInfoLiveData() {
        userMediatorLiveData.addSource(userRepository.userInfoLiveData(), userMediatorLiveData::setValue);
        return userMediatorLiveData;
    }

    public void setProfilePic(Uri uri) {
        userRepository.setUserProfilePic(uri);
    }

    public void addToCart(int id, int maxStock, int price, String name) {
        // Check the stock before adding
        userRepository.addToCart(id, maxStock, price, name);
    }

    public LiveData<List<Cart>> getCartProducts() {
        return userRepository.getMyCart();
    }

    public LiveData<Integer> getProductQty(int productId) {
        return userRepository.productQty(productId);
    }

    public void removeFromCart(int id) {
        userRepository.removeFromCart(id);
    }

    public void deleteFromCart(int id) {
        userRepository.deleteFromCart(id);
    }

    public void orderProduct(String orderId, int productId, int price, String status, String address) {
        userRepository.singleProductOrder(orderId, productId, price, status, address);
    }

    public MutableLiveData<Boolean> getProgressBar() {
        return userRepository.getProgressbarObservable();
    }

    public void getSingleProductOrders() {
        userRepository.getSingleProductOrders();
    }

    public LiveData<List<Order>> getOrder() {
        return orderLiveData;
    }

    public void buyAllProductsFromCart(String orderId, String address, String status, int price,
                                       List<Cart> cart){
        userRepository.bulkProductOrder(orderId, address, status, price, cart);
    }
    public LiveData<List<Order>> getCartProductsOrdered(){
        return userRepository.getBulkProducts();
    }

    public LiveData<List<Cart>> getOrderProducts(String orderId) {
        return userRepository.getOrderProducts(orderId);
    }

    public void signOut(){
        userRepository.signOut();
    }


    public void getUserRole(){
        userRepository.getUserRole();
    }

    public LiveData<String> getLiveData() {
        return liveData;
    }

    public LiveData<List<Order>> getAllOrdersLiveData() {
        return allOrdersLiveData;
    }
}
