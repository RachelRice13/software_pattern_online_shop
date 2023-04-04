package com.example.software_pattern_online_shop.HomePage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CustomerHomeFragment extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<StockItem> stockItems;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private StockItemAdapter stockItemAdapter;
    private TextView noItemsMessageTV;

    public CustomerHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        noItemsMessageTV = view.findViewById(R.id.no_items_message_tv);
        buildRecyclerView();

        if (stockItems.size() == 0) {
            noItemsMessageTV.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void populateList() {
        Query query = firebaseFirestore.collection("items").orderBy("price", Query.Direction.ASCENDING);
        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()) {
                    if (change.getType() == DocumentChange.Type.ADDED) {
                        StockItem stockItem = change.getDocument().toObject(StockItem.class).withId(change.getDocument().getId());
                        stockItems.add(stockItem);
                        stockItemAdapter.notifyDataSetChanged();
                        hideMessage(stockItems.size());
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void hideMessage(int size) {
        if (size != 0) {
            noItemsMessageTV.setVisibility(View.INVISIBLE);
        }
    }

    private void buildRecyclerView() {
        stockItems = new ArrayList<>();
        recyclerView = view.findViewById(R.id.stock_items_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(view.getContext());
        stockItemAdapter = new StockItemAdapter(stockItems, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockItemAdapter);
        populateList();
    }
}