package com.example.ibuy.views.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ibuy.R;
import com.example.ibuy.adapters.ProductNameAdapter;
import com.example.ibuy.classes.RunTimePermissionWrapper;
import com.example.ibuy.models.Cart;
import com.example.ibuy.utils.Utils;
import com.example.ibuy.viewmodels.UserManagerModelView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class ReceiptActivity extends AppCompatActivity {

    private TextView userName;
    private String pdfPath;
    private Bitmap bitmap;

    private MaterialButton download;
    private static final String[] WALK_THROUGH = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_receipt);

        TextView orderId = findViewById(R.id.order_id);
        TextView date = findViewById(R.id.date);
        userName = findViewById(R.id.user_name);
        TextView userAddress = findViewById(R.id.user_address);
        TextView status = findViewById(R.id.status);
        RelativeLayout singleProductView = findViewById(R.id.single_product_view);
        RelativeLayout multipleProductView = findViewById(R.id.multiple_product_view);
        TextView productName = findViewById(R.id.product_name);
        TextView productPrice = findViewById(R.id.product_price);
        download = findViewById(R.id.download);
        TextView totalPrice = findViewById(R.id.total_price);
        RecyclerView productsName = findViewById(R.id.product_names);
        productsName.setLayoutManager(new LinearLayoutManager(this));
        productsName.setHasFixedSize(true);

        UserManagerModelView userManagerModelView = new ViewModelProvider(this)
                .get(UserManagerModelView.class);

        Intent intent = getIntent();
        if (Objects.requireNonNull(intent.getStringExtra(getString(R.string.from)))
                .equals(getString(R.string.fragment))
        && Objects.requireNonNull(intent.getStringExtra(getString(R.string.type)))
                .equals(getString(R.string.single_buy))) {
            singleProductView.setVisibility(View.VISIBLE);
            multipleProductView.setVisibility(View.GONE);

            String id = intent.getStringExtra(getString(R.string.order_id));
            long dateCreated = intent.getLongExtra(getString(R.string.created_date), 0L);
            String address = intent.getStringExtra(getString(R.string.address).toLowerCase());
            String orderStatus = intent.getStringExtra(getString(R.string.status));
            String name = intent.getStringExtra(getString(R.string.name));
            int price = intent.getIntExtra(getString(R.string.price), 0);
            orderId.setText(id);
            date.setText(Utils.getDate(dateCreated));
            userAddress.setText(address);
            status.setText(Objects.requireNonNull(orderStatus).toUpperCase());
            productName.setText(name);
            productPrice.setText(MessageFormat.format("${0}", price));
            totalPrice.setText(MessageFormat.format("${0}", price));
            userManagerModelView.getUserInfoLiveData().observe(this, user ->
                    userName.setText(MessageFormat.format("{0} {1}", Utils.decrypt(user.getFirst_name())
                    , Utils.decrypt(user.getLast_name()))));
        }else{
            singleProductView.setVisibility(View.GONE);
            multipleProductView.setVisibility(View.VISIBLE);
            List<Cart> arraylist = (List<Cart>) intent.getSerializableExtra(getString(R.string.products_list));
            ProductNameAdapter productNameAdapter = new ProductNameAdapter(arraylist);
            productsName.setAdapter(productNameAdapter);
            String id = intent.getStringExtra(getString(R.string.order_id));
            long dateCreated = intent.getLongExtra(getString(R.string.created_date), 0L);
            String address = intent.getStringExtra(getString(R.string.address).toLowerCase());
            String orderStatus = intent.getStringExtra(getString(R.string.status));
            int price = intent.getIntExtra(getString(R.string.price), 0);
            orderId.setText(id);
            date.setText(Utils.getDate(dateCreated));
            userAddress.setText(address);
            status.setText(Objects.requireNonNull(orderStatus).toUpperCase());
            totalPrice.setText(MessageFormat.format("${0}", price));
            userManagerModelView.getUserInfoLiveData().observe(this, user ->
                    userName.setText(MessageFormat.format("{0} {1}", Utils.decrypt(user.getFirst_name())
                            , Utils.decrypt(user.getLast_name()))));
        }

        download.setOnClickListener(v -> askPermission());
    }

//    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {
//
//        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(returnedBitmap);
//        Drawable bgDrawable = view.getBackground();
//
//        if (bgDrawable != null) {
//            bgDrawable.draw(canvas);
//        } else {
//            canvas.drawColor(Color.WHITE);
//        }
//
//        view.draw(canvas);
//        return returnedBitmap;
//    }

    private void createPdf() {

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(),
                bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);

        Bitmap bitmap = Bitmap.createScaledBitmap(this.bitmap,
                this.bitmap.getWidth(), this.bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);
        File filePath = new File(pdfPath);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, document.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, getString(R.string.errata) + e.toString(), Toast.LENGTH_SHORT).show();
        }

        document.close();

//        if (imagePathFile.exists())
//            imagePathFile.delete();
        openPdf(pdfPath);
    }

    private void openPdf(String path) {
        File file = new File(path);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() +
                ".provider", file);
        target.setDataAndType(photoURI, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, getString(R.string.open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_app_pdf, Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        if (RunTimePermissionWrapper.isAllPermissionGranted(this, WALK_THROUGH)) {
            download.setVisibility(View.GONE);
            //shareScreenShotM(getWindow().getDecorView().getRootView(),  constraintLayout);
            takeScreenShot();
            download.setVisibility(View.VISIBLE);
        }
        RunTimePermissionWrapper.handleRunTimePermission(ReceiptActivity.this,
                RunTimePermissionWrapper.REQUEST_CODE.MULTIPLE_WALKTHROUGH, WALK_THROUGH);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!RunTimePermissionWrapper.isAllPermissionGranted(this, WALK_THROUGH)) {
            showSnack(ActivityCompat.shouldShowRequestPermissionRationale(this, WALK_THROUGH[0]));
        }
    }

    private void showSnack(final boolean isRationale) {
        final Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(),
                R.string.internal_storage, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(isRationale ? "VIEW" : "Settings", view -> {
            snackbar.dismiss();

            if (isRationale)
                RunTimePermissionWrapper.handleRunTimePermission(ReceiptActivity.this,
                        RunTimePermissionWrapper.REQUEST_CODE.MULTIPLE_WALKTHROUGH, WALK_THROUGH);
            else
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 1001);
        });

        snackbar.show();
    }

//    public void shareScreenShotM(View view, NestedScrollView constraintLayout){
//
//        Bitmap bm = takeScreenShot(view,constraintLayout);  //method to take screenshot
//        File file = savePic(bm);  // method to save screenshot in phone.
//        Log.i("MESSI", file.getAbsolutePath());
//    }

//    public Bitmap takeScreenShot(View u,  NestedScrollView z){
//
//        u.setDrawingCacheEnabled(true);
//        int totalHeight = z.getChildAt(0).getHeight();
//        int totalWidth = z.getChildAt(0).getWidth();
//
//        Log.d("yoheight",""+ totalHeight);
//        Log.d("yowidth",""+ totalWidth);
//        u.layout(0, 0, totalWidth, totalHeight);
//        u.buildDrawingCache();
//        Bitmap b = Bitmap.createBitmap(u.getDrawingCache());
//        u.setDrawingCacheEnabled(false);
//        u.destroyDrawingCache();
//        return b;
//    }

    private void takeScreenShot(){

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ScreenShot/");
        pdfPath = folder.getAbsolutePath();
        String file_name = "Screenshot";
        pdfPath = pdfPath + "/" + file_name + System.currentTimeMillis() + ".pdf";

        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        String extr = Environment.getExternalStorageDirectory() + "/Invoice/";
       // String fileName = file_name + ".jpg";
       // File imagePathFile = new File(extr, fileName);
       // File imagePathFile = null;
       // bitmap = getBitmapFromView(u, rootHeight, rootWidth);

        try{
            File imagePathFile = new File(extr, System.currentTimeMillis() + ".jpg");
            //imagePathFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(imagePathFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        createPdf();


    }

//    public static File savePic(Bitmap bm) {
//
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + "/Foldername");
//
//        if (!sdCardDirectory.exists()) {
//            sdCardDirectory.mkdirs();
//        }
//        //  File file = new File(dir, fileName);
//        File file = null;
//        try {
//            file = new File(sdCardDirectory, Calendar.getInstance()
//                    .getTimeInMillis() + ".jpg");
//            file.createNewFile();
//            new FileOutputStream(file).write(bytes.toByteArray());
//            Log.d("Fabsolute", "File Saved::--->" + file.getAbsolutePath());
//            Log.d("Sabsolute", "File Saved::--->" + sdCardDirectory.getAbsolutePath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return file;
//    }

}
