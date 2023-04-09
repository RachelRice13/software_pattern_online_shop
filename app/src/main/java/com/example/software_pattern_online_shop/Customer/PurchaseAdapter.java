package com.example.software_pattern_online_shop.Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.Model.Purchase;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ExampleViewHolder>{
    private List<Purchase> purchases;
    private Context context;

    public PurchaseAdapter(List<Purchase> purchases, Context context) {
        this.purchases = purchases;
        this.context = context;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, priceTv, itemsTv, cardNumberTv, expiryDateTv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            dateTv = itemView.findViewById(R.id.purchase_history_date);
            priceTv = itemView.findViewById(R.id.purchase_history_price);
            itemsTv = itemView.findViewById(R.id.items_tv);
            cardNumberTv = itemView.findViewById(R.id.card_number_tv);
            expiryDateTv = itemView.findViewById(R.id.expiry_date_tv);
            cardView = itemView.findViewById(R.id.purchase_history_cv);
        }
    }

    @NonNull
    @Override
    public PurchaseAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payment_history_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.ExampleViewHolder holder, int position) {
        Purchase purchase = purchases.get(position);
        holder.dateTv.setText(purchase.getDate());
        holder.priceTv.setText("€" + purchase.getTotalPrice());

        PaymentMethod paymentMethod = purchase.getPaymentMethod();
        String lastThreeDigits = paymentMethod.getCardNumber().substring(12,15);
        holder.cardNumberTv.setText("Card ending with: " + lastThreeDigits);
        holder.expiryDateTv.setText("Expiry Date: " + paymentMethod.getExpiryDate());

        String itemsString = "";
        ArrayList<BasketItem> items = purchase.getItems();
        for (BasketItem basketItem : items) {
            itemsString += basketItem.getItemTitle() + "\t\t" + "€" + basketItem.getPrice() + "\t\t" + basketItem.getQuantity() + " items\n";
        }
        holder.itemsTv.setText(itemsString);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }
}
