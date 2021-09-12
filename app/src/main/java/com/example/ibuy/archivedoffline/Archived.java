package com.example.ibuy.archivedoffline;

//import android.app.Application;
//import android.os.AsyncTask;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import com.example.ibuy.dao.ProductDao;
//import com.example.ibuy.db.ProductDatabase;
//import com.example.ibuy.models.ProductServiceResponse;
//import com.example.ibuy.api.ProductService;
//import com.example.ibuy.api.RetrofitService;
//import com.example.ibuy.models.Product;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

//public class ProductRepository {
//
//    private final ProductService productService;
//    private final MutableLiveData<ProductServiceResponse> searchedProductLiveData = new MutableLiveData<>();
//    private final MutableLiveData<ProductServiceResponse> productDetailsLiveData = new MutableLiveData<>();
//
//    // Impl
//    private final ProductDao productDao;
//    private final LiveData<List<Product>> allProducts;
//
//    public ProductRepository(Application application){
//        productService = RetrofitService.getService();
//        ProductDatabase database = ProductDatabase.getInstance(application);
//        productDao = database.productDAO();
//        allProducts = productDao.getAllProducts();
//    }

//
//    // To Remove if not working
//    public void insertProduct(List<Product> product){
//        new com.example.ibuy.repositories.ProductRepository.InsertProductAsyncTask(productDao).execute(product);
//    }
//
//    public void getProductById(int id){
//        new com.example.ibuy.repositories.ProductRepository.GetProductAsyncTask(productDao).execute(id);
//    }
//
//    public LiveData<List<Product>> getAllProducts2() {
//        return allProducts;
//    }
//
//    public void getAllProducts(){
//        MutableLiveData<ProductServiceResponse> productsLiveData = new MutableLiveData<>();
//        productService.getAllProducts().enqueue(new Callback<List<Product>>() {
//            @Override
//            public void onResponse(@NotNull Call<List<Product>> call, @NotNull Response<List<Product>> response) {
//                if(response.isSuccessful()){
//                    insertProduct(response.body());
//                }
//            }
//            @Override
//            public void onFailure(@NotNull Call<List<Product>> call, @NotNull Throwable t) {
//                productsLiveData.postValue(new ProductServiceResponse(t));
//            }
//        });
//        // return productsLiveData;
//    }
//
////
//
//    private static class InsertProductAsyncTask extends AsyncTask<List<Product>, Void, Void> {
//        private final ProductDao productDao;
//
//        private InsertProductAsyncTask(ProductDao productDao){
//            this.productDao = productDao;
//        }
//
//        @SafeVarargs
//        @Override
//        protected final Void doInBackground(List<Product>... products) {
//            productDao.insertProduct(products[0]);
//            return null;
//        }
//    }
//
//    private static class GetProductAsyncTask extends AsyncTask<Integer, Void, Void>{
//        private final ProductDao productDao;
//
//        private GetProductAsyncTask(ProductDao productDao){
//            this.productDao = productDao;
//        }
//
//        @Override
//        protected Void doInBackground(Integer... ints) {
//            productDao.getProductById(ints[0]);
//            return null;
//        }
//    }
//}
