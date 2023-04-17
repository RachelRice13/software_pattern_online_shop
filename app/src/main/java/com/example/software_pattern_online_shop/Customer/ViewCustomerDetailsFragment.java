package com.example.software_pattern_online_shop.Customer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.software_pattern_online_shop.Model.Purchase;
import com.example.software_pattern_online_shop.Model.User;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewCustomerDetailsFragment extends Fragment {
    private static final String TAG = "ViewCustDetailsFragment";
    private View view;
    private ArrayList<Purchase> purchases;
    private PurchaseAdapter purchaseAdapter;
    private FragmentTransaction transaction;
    private User customer;
    private LinearLayout detailsLL, purchaseHistoryLL;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;

    public ViewCustomerDetailsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_customer_details, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        Bundle bundle = getArguments();
        customer = (User) bundle.getSerializable("customer");
        transaction = getFragmentManager().beginTransaction();
        detailsLL = view.findViewById(R.id.details_ll);
        purchaseHistoryLL = view.findViewById(R.id.purchase_history_ll);

        getCustomerDetails();
        getCustomerId();
        buttons();

        return view;
    }

    private void getCustomerDetails() {
        TextView fullNameTv, emailTv, addressTv;

        fullNameTv = view.findViewById(R.id.customer_full_name_tv);
        emailTv = view.findViewById(R.id.customer_email_tv);
        addressTv = view.findViewById(R.id.customer_address_tv);

        fullNameTv.setText(customer.getFirstName() + " " + customer.getSurname());
        emailTv.setText(customer.getEmail());
        addressTv.setText(customer.getAddress());
    }

    private void getCustomerId() {
        firebaseFirestore.collection("customer").whereEqualTo("email", customer.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                buildRecyclerView(documentSnapshot.getId());
                            }
                        }
                    }
                });
    }

    private void buildRecyclerView(String userId) {
        purchases = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.purchase_history_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        purchaseAdapter = new PurchaseAdapter(purchases, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(purchaseAdapter);
        populateList(userId);
    }

    private void populateList(String userId) {
        Query query = firebaseFirestore.collection("purchase_history").document(userId).collection("purchases").orderBy("date", Query.Direction.DESCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        Purchase purchase = change.getDocument().toObject(Purchase.class);
                        purchases.add(purchase);
                        purchaseAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void buttons() {
        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        Button detailsBut, purchaseHistoryBut;
        detailsBut = view.findViewById(R.id.details_button);
        purchaseHistoryBut = view.findViewById(R.id.purchase_history_button);

        detailsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLL.setVisibility(View.VISIBLE);
                purchaseHistoryLL.setVisibility(View.INVISIBLE);
            }
        });

        purchaseHistoryBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLL.setVisibility(View.INVISIBLE);
                purchaseHistoryLL.setVisibility(View.VISIBLE);
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new CustomerDetailsFragment()).commit();
            }
        });
    }
}