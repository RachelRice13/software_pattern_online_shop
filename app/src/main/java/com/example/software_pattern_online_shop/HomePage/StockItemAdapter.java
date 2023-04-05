package com.example.software_pattern_online_shop.HomePage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.example.software_pattern_online_shop.Stock.ViewStockItemFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StockItemAdapter extends RecyclerView.Adapter<StockItemAdapter.ExampleViewHolder> {
    private List<StockItem> stockItems;
    private Context context;
    private FragmentManager fragmentManager;
    private StorageReference storageReference;

    public StockItemAdapter (List<StockItem> stockItems, Context context, FragmentManager fragmentManager) {
        this.stockItems = stockItems;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantity;
        ImageView stockImage;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            title = itemView.findViewById(R.id.stock_item_title);
            price = itemView.findViewById(R.id.stock_item_price);
            quantity = itemView.findViewById(R.id.stock_item_quantity);
            stockImage = itemView.findViewById(R.id.stock_item_image);
            cardView = itemView.findViewById(R.id.stock_item_card_view);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stock_item_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        StockItem stockItem = stockItems.get(position);
        holder.title.setText(stockItem.getTitle());
        holder.price.setText("€ " + stockItem.getPrice());
        holder.quantity.setText(stockItem.getQuantity() + " Remaining");

        StorageReference stockImageRef = storageReference.child(stockItem.getImagePath());
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewStockItemFragment viewStockItemFragment = new ViewStockItemFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("stockItem", stockItem);
                viewStockItemFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, viewStockItemFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockItems.size();
    }

    public void filteredList(ArrayList<StockItem> filteredList) {
        stockItems = filteredList;
        notifyDataSetChanged();
    }
}