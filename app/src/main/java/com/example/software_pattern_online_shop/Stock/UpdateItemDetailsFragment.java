package com.example.software_pattern_online_shop.Stock;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.software_pattern_online_shop.Common.Validation;
import com.example.software_pattern_online_shop.HomePage.AdminHomeFragment;
import com.example.software_pattern_online_shop.Model.Comment;
import com.example.software_pattern_online_shop.Model.Rating;
import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("LongLogTag")
public class UpdateItemDetailsFragment extends Fragment {
    private static final String TAG = "UpdateItemDetailsFragment";
    private View view;
    private  FragmentTransaction transaction;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private StockItem stockItem;
    private EditText manufacturerET, priceET, quantityET, categoryET;
    private ImageView itemImageIv;
    private Uri imageUri;
    private TextInputLayout manufacturerLO, priceLO, quantityLO, categoryLO;

    public UpdateItemDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_item_details, container, false);

        Bundle bundle = getArguments();
        stockItem = (StockItem) bundle.getSerializable("stockItem");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        transaction = getFragmentManager().beginTransaction();

        manufacturerET = view.findViewById(R.id.update_manufacturer_et);
        manufacturerLO = view.findViewById(R.id.update_manufacturer);
        priceET = view.findViewById(R.id.update_price_et);
        priceLO = view.findViewById(R.id.update_price);
        quantityET = view.findViewById(R.id.update_quantity_et);
        quantityLO = view.findViewById(R.id.update_quantity);
        categoryET = view.findViewById(R.id.update_category_et);
        categoryLO = view.findViewById(R.id.update_category);
        itemImageIv = view.findViewById(R.id.item_image_iv);

        getItemDetails();

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new UpdateStockFragment()).commit();
            }
        });

        itemImageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        Button updateStockButton = view.findViewById(R.id.update_stock_button);
        updateStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStock();
            }
        });

        return view;
    }

    private void getItemDetails() {
        TextView titleTv = view.findViewById(R.id.update_title_tv);

        titleTv.setText(stockItem.getTitle());
        manufacturerET.setText(stockItem.getManufacturer());
        priceET.setText(String.valueOf(stockItem.getPrice()));
        quantityET.setText(String.valueOf(stockItem.getQuantity()));
        categoryET.setText(stockItem.getCategory());

        StorageReference stockImageRef = storageReference.child(stockItem.getImagePath());
        stockImageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        itemImageIv.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        itemImageIv.setImageResource(R.drawable.ic_image);
                    }
                });
    }

    private void choosePicture() {
        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(choosePictureIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            itemImageIv.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image....");
        progressDialog.show();

        StorageReference stockItemPictureRef = storageReference.child(stockItem.getImagePath());

        stockItemPictureRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(view, "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void updateStock() {
        String manufacturer = manufacturerET.getText().toString();
        String priceString = priceET.getText().toString();
        String quantityString = quantityET.getText().toString();
        String category = categoryET.getText().toString();

        boolean validManufacturer = Validation.validateBlank(manufacturer, manufacturerLO);
        boolean validPrice = Validation.validateNumber(priceString, priceLO);
        boolean validQuantity = Validation.validateNumber(quantityString, quantityLO);
        boolean validCategory = Validation.validateBlank(category, categoryLO);

        if (validManufacturer && validPrice && validQuantity && validCategory) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double price = Double.parseDouble(decimalFormat.format(Double.parseDouble(priceString)));
            int quantity = Integer.parseInt(quantityString);
            ArrayList<Rating> ratings = stockItem.getRatings();
            ArrayList<Comment> comments = stockItem.getComments();

            StockItem updateItem = new StockItem(stockItem.getTitle(), manufacturer, category, stockItem.getImagePath(), price, quantity, ratings, comments);
            updateFireStore(updateItem);
        }
    }

    private void updateFireStore(StockItem updateItem) {
        firebaseFirestore.collection("items").document(stockItem.StockItemId).set(updateItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(view, "Updated Item " + stockItem.getTitle(), Snackbar.LENGTH_LONG).show();
                        transaction.replace(R.id.container, new AdminHomeFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update stock item.", e);
                    }
                });
    }
}