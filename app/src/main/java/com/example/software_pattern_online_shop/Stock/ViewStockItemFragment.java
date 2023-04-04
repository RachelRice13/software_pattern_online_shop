package com.example.software_pattern_online_shop.Stock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.software_pattern_online_shop.HomePage.AdminHomeFragment;
import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewStockItemFragment extends Fragment {
    private static final String TAG = "ViewStockItemFragment";
    private View view;
    private StorageReference storageReference;

    public ViewStockItemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_stock_item, container, false);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        getItemDetails();

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new AdminHomeFragment()).commit();
            }
        });

        return view;
    }

    private void getItemDetails() {
        Bundle bundle = getArguments();
        StockItem stockItem = (StockItem) bundle.getSerializable("stockItem");
        TextView titleTv, manufacturerTv, priceTv, quantityTv, categoryTv;
        ImageView itemImageIv = view.findViewById(R.id.item_image_iv);

        titleTv = view.findViewById(R.id.item_title_tv);
        manufacturerTv = view.findViewById(R.id.item_manufacturer_tv);
        priceTv = view.findViewById(R.id.item_price_tv);
        quantityTv = view.findViewById(R.id.item_quantity_tv);
        categoryTv = view.findViewById(R.id.item_category_tv);

        titleTv.setText(stockItem.getTitle());
        manufacturerTv.setText(stockItem.getManufacturer());
        priceTv.setText(String.valueOf(stockItem.getPrice()));
        quantityTv.setText(String.valueOf(stockItem.getQuantity()));
        categoryTv.setText(stockItem.getCategory());

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
                        itemImageIv.setImageResource(R.drawable.ic_basket);
                    }
                });
    }
}