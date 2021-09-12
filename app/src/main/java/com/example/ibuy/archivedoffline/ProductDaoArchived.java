//package com.example.ibuy.dao;
//
//import androidx.lifecycle.LiveData;
//import androidx.room.Dao;
//import androidx.room.Delete;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//
//import com.example.ibuy.models.Product;
//
//import java.util.List;
//
//@Dao
//public interface ProductDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insertProduct(List<Product> product);
//
//    @Query("DELETE FROM product_table ")
//    void deleteAllProducts();
//
//    @Query("SELECT * FROM product_table")
//    LiveData<List<Product>> getAllProducts();
//
//    @Query("SELECT * FROM product_table WHERE product_id = :id")
//    Product getProductById(int id);
//
//}
