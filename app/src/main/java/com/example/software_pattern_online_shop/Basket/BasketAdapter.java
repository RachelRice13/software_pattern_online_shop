package com.example.software_pattern_online_shop.Basket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ExampleViewHolder> {
    private List<BasketItem> basket;
    private Context context;
    private StorageReference storageReference;

    public BasketAdapter (List<BasketItem> basket, Context context) {
        this.basket = basket;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantity, deleteButton;
        ImageView stockImage;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            title = itemView.findViewById(R.id.basket_item_title);
            price = itemView.findViewById(R.id.basket_item_price);
            quantity = itemView.findViewById(R.id.basket_item_quantity);
            stockImage = itemView.findViewById(R.id.basket_item_image);
            deleteButton = itemView.findViewById(R.id.basket_item_delete);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.basket_item_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        BasketItem basketItem = basket.get(position);
        holder.title.setText(basketItem.getItemTitle());
        holder.price.setText("€" + basketItem.getPrice());
        holder.quantity.setText("Quantity: " + basketItem.getQuantity());

        StorageReference stockImageRef = storageReference.child(basketItem.getImagePath());
        stockImageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.stockImage.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.stockImage.setImageResource(R.drawable.ic_image);
                    }
                });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Delete Item", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return basket.size();
    }
}