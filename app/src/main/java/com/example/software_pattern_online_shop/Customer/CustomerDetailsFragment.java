package com.example.software_pattern_online_shop.Customer;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.software_pattern_online_shop.Model.User;
import com.example.software_pattern_online_shop.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomerDetailsFragment extends Fragment {
    private View view;
    private ArrayList<User> customers;
    private CustomerAdapter customerAdapter;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    public CustomerDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_details, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        buildRecyclerView();

        return view;
    }

    private void buildRecyclerView() {
        customers = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.customer_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        customerAdapter = new CustomerAdapter(customers, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(customerAdapter);
        populateList();
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("customer").orderBy("firstName", Query.Direction.ASCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        User customer = change.getDocument().toObject(User.class);
                        customers.add(customer);
                        customerAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }
}