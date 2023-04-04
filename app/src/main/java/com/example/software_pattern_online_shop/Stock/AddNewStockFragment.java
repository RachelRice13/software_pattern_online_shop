package com.example.software_pattern_online_shop.Stock;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.example.software_pattern_online_shop.HomePage.AdminHomeFragment;
import com.example.software_pattern_online_shop.LoginRegister.RegisterActivity;
import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DecimalFormat;
import java.util.UUID;

public class AddNewStockFragment extends Fragment {
    private static final String TAG = "AddNewStockFragment";
    private View view;
    private FragmentTransaction transaction;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ImageView stockItemImageIV;
    private Uri imageUri;
    private String imageName = " ", imagePath = " ";
    private TextInputLayout titleLO, manufacturerLO, priceLO, quantityLO, categoryLO;

   public AddNewStockFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_new_stock, container, false);

        transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        stockItemImageIV = view.findViewById(R.id.upload_image_iv);
        titleLO = view.findViewById(R.id.add_stock_title);
        manufacturerLO = view.findViewById(R.id.add_stock_manufacturer);
        priceLO = view.findViewById(R.id.add_stock_price);
        quantityLO = view.findViewById(R.id.add_stock_quantity);
        categoryLO = view.findViewById(R.id.add_stock_category);

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new AdminHomeFragment()).commit();
            }
        });

        stockItemImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        Button addNewStockButton = view.findViewById(R.id.add_new_stock_button);
        addNewStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewStock();
            }
        });

        return view;
    }

    private void choosePicture() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View view = layoutInflater.inflate(R.layout.add_stock_image, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        EditText imageNameET = view.findViewById(R.id.image_name_et);
        TextInputLayout imageNameLo = view.findViewById(R.id.image_name);
        FloatingActionButton goBack = view.findViewById(R.id.go_back);
        Button choosePictureButton = view.findViewById(R.id.choose_image_button);

        alertDialog.setCancelable(false).setView(view);
        AlertDialog alert = alertDialog.create();
        alert.show();

        choosePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageName = imageNameET.getText().toString();
                boolean validImageName = RegisterActivity.validateBlank(imageName, imageNameLo);

                if (validImageName) {
                    Intent choosePictureIntent = new Intent();
                    choosePictureIntent.setType("image/*");
                    choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(choosePictureIntent, 1);
                    alert.cancel();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            stockItemImageIV.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image....");
        progressDialog.show();

        String randomString = UUID.randomUUID().toString();
        StorageReference stockItemPictureRef = storageReference.child("stock_items_images/").child(randomString + "_" + imageName);
        imagePath = stockItemPictureRef.getPath();

        TextView uploadImageTV = view.findViewById(R.id.upload_image_tv);

        stockItemPictureRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(view, "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        uploadImageTV.setVisibility(View.INVISIBLE);
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

    private void addNewStock() {
        EditText titleET, manufacturerET, priceET, quantityET, categoryET;
        titleET = view.findViewById(R.id.add_stock_title_et);
        manufacturerET = view.findViewById(R.id.add_stock_manufacturer_et);
        priceET = view.findViewById(R.id.add_stock_price_et);
        quantityET = view.findViewById(R.id.add_stock_quantity_et);
        categoryET = view.findViewById(R.id.add_stock_category_et);

        String title = titleET.getText().toString();
        String manufacturer = manufacturerET.getText().toString();
        String priceString = priceET.getText().toString();
        String quantityString = quantityET.getText().toString();
        String category = categoryET.getText().toString();

        boolean validTitle = RegisterActivity.validateBlank(title, titleLO);
        boolean validManufacturer = RegisterActivity.validateBlank(manufacturer, manufacturerLO);
        boolean validPrice = validateNumber(priceString, priceLO);
        boolean validQuantity = validateNumber(quantityString, quantityLO);
        boolean validCategory = RegisterActivity.validateBlank(category, categoryLO);
        boolean validImage = validateImage(imagePath);

        if (validTitle && validManufacturer && validPrice && validQuantity && validCategory && validImage) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double price = Double.parseDouble(decimalFormat.format(Double.parseDouble(priceString)));
            int quantity = Integer.parseInt(quantityString);
            StockItem stockItem = new StockItem(title, manufacturer, category, imagePath, price, quantity);
            addToFireStore(stockItem);
        }
    }

    private boolean validateImage(String imagePath) {
       if (imagePath.equals(" ")) {
           Toast.makeText(getContext(), "Choose an image", Toast.LENGTH_SHORT).show();
           return false;
       } else {
           return true;
       }
    }

    private boolean validateNumber(String number, TextInputLayout layout) {
       if (number.isEmpty()) {
           layout.setError("This is Required");
           return false;
       } else {
           String firstDigit = number.substring(0,1);
           if (firstDigit.equals("0")) {
               layout.setError("Number must be greater than 0");
               return false;
           } else {
               layout.setError(null);
               return true;
           }
       }
    }

    public void addToFireStore(StockItem stockItem) {
        firebaseFirestore.collection("stock_items")
                .add(stockItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Log.d(TAG, "Added Stock Item to DB");
                        Toast.makeText(getContext(), "Added Stock Item to DB", Toast.LENGTH_SHORT).show();
                        transaction.replace(R.id.container, new AdminHomeFragment()).commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Add Stock to DB", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }
}