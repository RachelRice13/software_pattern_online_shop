package com.example.software_pattern_online_shop.Stock;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class UpdateStockAdapter extends RecyclerView.Adapter<UpdateStockAdapter.ExampleViewHolder> {
    private View view;
    private List<StockItem> stockItems;
    private Context context;
    private FragmentManager fragmentManager;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    public UpdateStockAdapter (List<StockItem> stockItems, Context context, FragmentManager fragmentManager) {
        this.stockItems = stockItems;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantity;
        ImageView stockImage;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference();
            firebaseFirestore = FirebaseFirestore.getInstance();
            title = itemView.findViewById(R.id.stock_item_title);
            price = itemView.findViewById(R.id.stock_item_price);
            quantity = itemView.findViewById(R.id.stock_item_quantity);
            stockImage = itemView.findViewById(R.id.stock_item_image);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.stock_item_row_layout, parent, false);
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
    }

    @Override
    public int getItemCount() {
        return stockItems.size();
    }

    public Context getContext() {
        return context;
    }

    public void deleteItem(int position) {
        StockItem stockItem = stockItems.get(position);
        firebaseFirestore.collection("items").document(stockItem.StockItemId).delete();
        stockItems.remove(position);
        notifyItemRemoved(position);
        Snackbar.make(view, "Deleted item", Snackbar.LENGTH_LONG).show();
    }

    public void updateItemDetails(int position) {
        StockItem stockItem = stockItems.get(position);
        UpdateItemDetailsFragment updateItemDetailsFragment = new UpdateItemDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("stockItem", stockItem);
        updateItemDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.container, updateItemDetailsFragment).commit();
    }

}
