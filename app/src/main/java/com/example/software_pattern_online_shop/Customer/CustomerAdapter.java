package com.example.software_pattern_online_shop.Customer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.Customer;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ExampleViewHolder> {
    private List<Customer> customers;
    private Context context;
    private FragmentManager fragmentManager;

    public CustomerAdapter(List<Customer> customers, Context context, FragmentManager fragmentManager) {
        this.customers = customers;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTv;
        MaterialCardView cardView;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            fullNameTv = itemView.findViewById(R.id.full_name_tv);
            cardView = itemView.findViewById(R.id.profile_cv);
        }
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_row_layout, parent, false);
        return new ExampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExampleViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.fullNameTv.setText(customer.getFirstName() + " " + customer.getSurname());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCustomerDetailsFragment viewCustomerDetailsFragment = new ViewCustomerDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                viewCustomerDetailsFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.container, viewCustomerDetailsFragment).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }
}
