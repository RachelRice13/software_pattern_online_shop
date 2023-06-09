package com.example.software_pattern_online_shop.Stock;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class UpdateStockFragment extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<StockItem> stockItems;
    private UpdateStockAdapter updateStockAdapter;

    public UpdateStockFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_stock, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        buildRecyclerView();

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
                        updateStockAdapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }

    private void buildRecyclerView() {
        stockItems = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.stock_items_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        updateStockAdapter = new UpdateStockAdapter(stockItems, getContext(), getFragmentManager());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new StockItemTouchHelper(updateStockAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(updateStockAdapter);
        populateList();
    }
}