package com.example.software_pattern_online_shop.Basket;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BasketFragment extends Fragment {
    private static final String TAG = "BasketFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ListenerRegistration listenerRegistration;
    private ArrayList<BasketItem> basket;
    private BasketAdapter basketAdapter;
    private String userId;

    public BasketFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_basket, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        buildRecyclerView();

       return view;
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("customer").document(userId).collection("basket").orderBy("itemTitle", Query.Direction.ASCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        BasketItem basketItem = change.getDocument().toObject(BasketItem.class).withId(change.getDocument().getId());
                        basket.add(basketItem);
                        basketAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void buildRecyclerView() {
        basket = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.basket_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        basketAdapter = new BasketAdapter(basket, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(basketAdapter);
        populateList();
    }
}