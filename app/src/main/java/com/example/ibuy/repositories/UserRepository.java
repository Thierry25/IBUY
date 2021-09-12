package com.example.ibuy.repositories;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.ibuy.R;
import com.example.ibuy.models.Cart;
import com.example.ibuy.models.Order;
import com.example.ibuy.models.User;
import com.example.ibuy.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

// Add value to allow users to see loading screen
public class UserRepository {
    private final Application application;
    private final MutableLiveData<FirebaseUser> userMutableLiveData;
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference userDatabaseReference;
    private final DatabaseReference cartDatabaseReference;
    private final DatabaseReference ordersDatabaseReference;
    private final DatabaseReference ordersDetailsDatabaseReference;
    private final StorageReference userStorageReference;
    private final MutableLiveData<Boolean> progressbarObservable;
    private final MutableLiveData<List<Order>> orderLiveData;
    private final MutableLiveData<String> liveData;

    public UserRepository(@NotNull Application application) {
        this.application = application;
        liveData = new MutableLiveData<>();
        userMutableLiveData = new MutableLiveData<>();
        progressbarObservable = new MutableLiveData<>();
        orderLiveData = new MutableLiveData<>();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        userDatabaseReference = firebaseDatabase.getReference(application.getString(R.string.users));
        userDatabaseReference.keepSynced(true);
        cartDatabaseReference = firebaseDatabase
                .getReference(application.getString(R.string.cart).toLowerCase());
        cartDatabaseReference.keepSynced(true);
        ordersDatabaseReference = firebaseDatabase.getReference(application.getString(R.string.orders));
        ordersDetailsDatabaseReference = firebaseDatabase
                .getReference(application.getString(R.string.order_details));
        userStorageReference = FirebaseStorage.getInstance()
                .getReference(application.getString(R.string.pictures));
        Utils.generateKey(application);
    }

    public void registerUser(String firstName, String lastName, String address,
                             String email, String password) {
        progressbarObservable.setValue(true);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        String[] encryptedValues = {Utils.encrypt(firstName), Utils.encrypt(lastName)
                                , Utils.encrypt(address), Utils.encrypt(email)};
                        uploadInfo(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid(),
                                encryptedValues[0], encryptedValues[1], encryptedValues[2],
                                encryptedValues[3], application.getString(R.string.profile_pic_default), application.getString(R.string.user));
                    } else {
                        try {
                            progressbarObservable.setValue(false);
                            throw Objects.requireNonNull(task.getException());
                        }
                        // if password entered is less than 6 characters
                        catch (FirebaseAuthWeakPasswordException weakPassword) {
                            Toast.makeText(application, weakPassword.getReason(),
                                    Toast.LENGTH_SHORT).show();
                            progressbarObservable.setValue(false);
                        }
                        // if email entered is a malformed email
                        catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                            Toast.makeText(application, malformedEmail.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressbarObservable.setValue(false);
                        }
                        // if email entered already exist in DB
                        catch (FirebaseAuthUserCollisionException existEmail) {
                            Toast.makeText(application, existEmail.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressbarObservable.setValue(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(application, application.getString(R.string.registration_error),
                                Toast.LENGTH_SHORT).show();
                    }
                    progressbarObservable.setValue(false);
                });
    }


    public void loginUser(String email, String password) {
        progressbarObservable.setValue(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        userMutableLiveData.postValue(firebaseAuth.getCurrentUser());
                        progressbarObservable.setValue(false);
                    } else {
                        try {
                            progressbarObservable.setValue(false);
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthInvalidCredentialsException firebaseAuthInvalidCredentialsException) {
                            Toast.makeText(application, firebaseAuthInvalidCredentialsException.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                            progressbarObservable.setValue(false);
                        } catch (FirebaseAuthInvalidUserException firebaseAuthInvalidUserException) {
                            Toast.makeText(application, firebaseAuthInvalidUserException.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                            progressbarObservable.setValue(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressbarObservable.setValue(false);
                        }
                    }
                }
        );
    }

    public void uploadInfo(String id, String firstName, String lastName, String address, String email,
                           String profile_pic, String role) {
        Map<String, Object> userMap = new HashMap();
        userMap.put("first_name", firstName);
        userMap.put("last_name", lastName);
        userMap.put("address", address);
        userMap.put("email", email);
        userMap.put("profile_pic", profile_pic);
        userMap.put("role", role);
        userDatabaseReference.child(id).setValue(userMap);
    }

    public FirebaseUser getSignedInUser() {
        return firebaseAuth.getCurrentUser();
    }

    public LiveData<User> userInfoLiveData() {
        MutableLiveData<User> userInfoMutableLiveData = new MutableLiveData<>();
        if (getSignedInUser() != null) {
            userDatabaseReference.child(getSignedInUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User userInfo = snapshot.getValue(User.class);
                            userInfoMutableLiveData.postValue(userInfo);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        return userInfoMutableLiveData;
    }

    public MutableLiveData<FirebaseUser> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void setUserProfilePic(Uri uri) {
        // Getting the actual file from the path
        File thumbImagePath = new File(uri.getPath());
        Bitmap thumbBitmap = null;
        try {
            thumbBitmap = new Compressor(application)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(75)
                    .compressToBitmap(thumbImagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (thumbBitmap != null) {
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        }
        final byte[] thumbByte = baos.toByteArray();
        if (getSignedInUser() != null) {
            final StorageReference thumbFilePath = userStorageReference
                    .child(getSignedInUser().getUid() + ".jpg");
            UploadTask uploadTask = thumbFilePath.putBytes(thumbByte);
            uploadTask.addOnSuccessListener(task -> {
                Task<Uri> thumbnailUpload = task.getStorage().getDownloadUrl();
                thumbnailUpload.addOnSuccessListener(uri1 -> {
                    String downloadUrl = uri1.toString();
                    userDatabaseReference.child(getSignedInUser().getUid())
                            .child(application.getString(R.string.profile_pic)).setValue(downloadUrl);
                }).addOnFailureListener(exception ->
                        Toast.makeText(application, exception.getMessage(),
                                Toast.LENGTH_SHORT).show());
            });
        }
    }

    public void addToCart(int id, int maxStock, int price, String name) {
        // check first if user_ id exist
        Map<String, Object> productMap = new HashMap<>();
        productMap.put(application.getString(R.string.prod_id_db), id);
        productMap.put(application.getString(R.string.qty_db), 1);
        productMap.put(application.getString(R.string.price), price);
        productMap.put(application.getString(R.string.name), name);

        if (getSignedInUser() != null) {
            // first call getProductQty to get the amount saved in the db and also
            cartDatabaseReference.child(getSignedInUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    // overwrite value of qty
                                                    cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                                                            .child(application.getString(R.string.qty_db))
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    Integer v = snapshot.getValue(Integer.class);
                                                                    if (v == null) {
                                                                        return;
                                                                    }
                                                                    if ((v + 1) > maxStock) {
                                                                        Toast.makeText(application, R.string.max_stock
                                                                                , Toast.LENGTH_SHORT).show();
                                                                    } else {
                                                                        cartDatabaseReference.child(getSignedInUser().getUid())
                                                                                .child(String.valueOf(id))
                                                                                .child(application.getString(R.string.qty_db))
                                                                                .setValue(v + 1);
                                                                        Toast.makeText(application,
                                                                                R.string.add_to_cart_success,
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                } else {
                                                    // add default value
                                                    cartDatabaseReference.child(getSignedInUser().getUid())
                                                            .child(String.valueOf(id)).updateChildren(productMap);
                                                    Toast.makeText(application,
                                                            R.string.add_to_cart_success, Toast.LENGTH_SHORT).show();

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                            } else {
                                cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                                        .updateChildren(productMap);
                                Toast.makeText(application, R.string.add_to_cart_success,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
        }
    }

    public void removeFromCart(int id) {
        if (getSignedInUser() != null) {
            cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                    .child(application.getString(R.string.qty_db))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer value = snapshot.getValue(Integer.class);
                            if (value == null) {
                                return;
                            }
                            if (value > 1) {
                                cartDatabaseReference.child(getSignedInUser().getUid())
                                        .child(String.valueOf(id))
                                        .child(application.getString(R.string.qty_db))
                                        .setValue(value - 1);
                            } else {
                                cartDatabaseReference.child(getSignedInUser().getUid())
                                        .child(String.valueOf(id)).removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    public void deleteFromCart(int id) {
        if (getSignedInUser() != null) {
            cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                    .removeValue();
            Toast.makeText(application, R.string.remove_successful, Toast.LENGTH_SHORT).show();
        }
    }

    // Should be MutableLiveData List<Cart>
    public LiveData<List<Cart>> getMyCart() {
        progressbarObservable.setValue(true);
        List<Cart> cartList = new ArrayList<>();
        MutableLiveData<List<Cart>> cartMutableLiveData = new MutableLiveData<>();
        if (getSignedInUser() != null) {
            cartDatabaseReference.child(getSignedInUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                Toast.makeText(application, R.string.empty_cart
                                        , Toast.LENGTH_SHORT).show();
                            } else {
                                for (DataSnapshot s : snapshot.getChildren()) {
                                    Cart myCartInfo = s.getValue(Cart.class);
                                    if (!cartList.contains(myCartInfo)) {
                                        // if crash verify here
                                        Objects.requireNonNull(myCartInfo)
                                                .setId(Integer.parseInt(Objects.requireNonNull(s.getKey())));
                                        cartList.add(myCartInfo);
                                    }
                                }
                                cartMutableLiveData.postValue(cartList);
                            }
                            progressbarObservable.setValue(false);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        return cartMutableLiveData;
    }

    public LiveData<Integer> productQty(int id) {
        MutableLiveData<Integer> quantityMutableLiveData = new MutableLiveData<>();
        if (getSignedInUser() != null) {
            cartDatabaseReference.child(getSignedInUser().getUid()).child(String.valueOf(id))
                    .child(application.getString(R.string.qty_db))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer qty = snapshot.getValue(Integer.class);
                            if (qty != null) {
                                quantityMutableLiveData.postValue(qty);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        return quantityMutableLiveData;
    }

    public void singleProductOrder(String orderId, int productId, int price, String status, String address) {
        progressbarObservable.setValue(true);
        Map<String, Object> orderRelatedInfo = new HashMap<>();
        orderRelatedInfo.put(application.getString(R.string.product), productId);
        orderRelatedInfo.put(application.getString(R.string.price), price);
        orderRelatedInfo.put(application.getString(R.string.status), status);
        orderRelatedInfo.put(application.getString(R.string.created_date), ServerValue.TIMESTAMP);
        orderRelatedInfo.put(application.getString(R.string.address).toLowerCase(), address);
        if (getSignedInUser() != null) {
            ordersDatabaseReference.child(getSignedInUser()
                    .getUid()).child(application.getString(R.string.single_buy))
                    .child(orderId).updateChildren(orderRelatedInfo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressbarObservable.setValue(false);
                }
            });
        }
    }


    // To verify if working properly in lab
    public void bulkProductOrder(String orderId, String address, String status, int price, List<Cart> cartList) {
        progressbarObservable.setValue(true);
        // Verify
        Map<String, Object> cartProducts = new HashMap<>();
        for (Cart cart : cartList) {
            cartProducts.put(String.valueOf(cart.getId()), cart);
        }
        ordersDetailsDatabaseReference.child(orderId).updateChildren(cartProducts);
        Map<String, Object> orderRelatedInfo = new HashMap<>();
        orderRelatedInfo.put(application.getString(R.string.address).toLowerCase(), address);
        orderRelatedInfo.put(application.getString(R.string.status).toLowerCase(), status);
        orderRelatedInfo.put(application.getString(R.string.created_date).toLowerCase(), ServerValue.TIMESTAMP);
        orderRelatedInfo.put(application.getString(R.string.price), price);
       // orderRelatedInfo.put(application.getString(R.string.products), cartProducts);
        if (getSignedInUser() != null) {
            ordersDatabaseReference.child(getSignedInUser().getUid()).child(application.getString(R.string.bulk))
                    .child(orderId).updateChildren(orderRelatedInfo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressbarObservable.setValue(false);
                    deleteMyCart();
                }
            });
        }
    }

    public void getSingleProductOrders() {
        if (getSignedInUser() != null) {
            List<Order> orderList = new ArrayList<>();
            progressbarObservable.setValue(true);
            ordersDatabaseReference.child(getSignedInUser().getUid())
                    .child(application.getString(R.string.single_buy)).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    // Log.i("MESSI1", snapshot.getValue().toString());

                    Order order = snapshot.getValue(Order.class);
                    if (order == null) {
                        return;
                    }
                    order.setOrder_id(snapshot.getKey());
                    orderList.add(order);
                    orderLiveData.postValue(orderList);
                    progressbarObservable.setValue(false);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void deleteMyCart() {
        cartDatabaseReference.child(getSignedInUser().getUid()).removeValue();
    }

    // Verify
    public LiveData<List<Order>> getBulkProducts() {
        MutableLiveData<List<Order>> bulkOrderLiveData = new MutableLiveData<>();
        if (getSignedInUser() != null) {
            List<Order> orderList = new ArrayList<>();
            progressbarObservable.setValue(true);
            ordersDatabaseReference.child(getSignedInUser().getUid())
                    .child(application.getString(R.string.bulk)).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) {
                        return;
                    }
                    order.setOrder_id(snapshot.getKey());
                    orderList.add(order);
                    bulkOrderLiveData.postValue(orderList);
                    progressbarObservable.setValue(false);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return bulkOrderLiveData;
    }

    public LiveData<List<Cart>> getOrderProducts(String orderId){
        MutableLiveData<List<Cart>> cartProductsLiveData = new MutableLiveData<>();
        if(getSignedInUser() != null){
            progressbarObservable.setValue(true);
            List<Cart> cartList = new ArrayList<>();
            ordersDetailsDatabaseReference.child(orderId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Cart cart = snapshot.getValue(Cart.class);
                    Objects.requireNonNull(cart)
                            .setId(Integer.parseInt(Objects.requireNonNull(snapshot.getKey())));
                    cartList.add(cart);
                    cartProductsLiveData.postValue(cartList);
                    progressbarObservable.setValue(false);
                }
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot,
                                           @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot,
                                         @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return cartProductsLiveData;
    }

    public LiveData<List<Order>> getOrderLiveData() {
        return orderLiveData;
    }

    public MutableLiveData<Boolean> getProgressbarObservable() {
        return progressbarObservable;
    }

    public void signOut(){
        if(getSignedInUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
    }

    public void getUserRole(){
        if(getSignedInUser()!= null){
            userDatabaseReference.child(getSignedInUser().getUid()).child("role").
                    addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    liveData.postValue(snapshot.getValue(String.class));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else{
            liveData.postValue("user");
        }
       // return liveData;
    }

    public LiveData<String> getLiveData() {
        return liveData;
    }

    public LiveData<List<Order>> getAllOrders(){
        MutableLiveData<List<Order>> allOrdersLiveData = new MutableLiveData<>();
        if(getSignedInUser() != null){
            List<Order> orderList = new ArrayList<>();
            ordersDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    for(DataSnapshot s: snapshot.getChildren()){
                        for(DataSnapshot s1: s.getChildren()){
                            Order order = s1.getValue(Order.class);
                            if (order == null) {
                                return;
                            }
                            order.setOrder_id(s1.getKey());
                            order.setUser_id(snapshot.getKey());
                            order.setType(s.getKey());
                            orderList.add(order);
                            allOrdersLiveData.postValue(orderList);
                            progressbarObservable.setValue(false);
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    return allOrdersLiveData;
    }
}
