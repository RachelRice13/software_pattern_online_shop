package com.example.software_pattern_online_shop.HomePage;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public abstract class HomeFragmentTemplate extends Fragment {
    private View view;
    private boolean sort = true;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<StockItem> stockItems;
    private StockItemAdapter stockItemAdapter;
    private TextView noItemsMessageTV;

    public void createHomePage(View view) {
        this.view = view;

        firebaseFirestore = FirebaseFirestore.getInstance();
        noItemsMessageTV = view.findViewById(R.id.no_items_message_tv);

        buildRecyclerView();
        search();
        sort();

        if (stockItems.size() == 0) {
            noItemsMessageTV.setVisibility(View.VISIBLE);
        }
    }

    private void buildRecyclerView() {
        stockItems = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.stock_items_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        stockItemAdapter = new StockItemAdapter(stockItems, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockItemAdapter);
        populateList();
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

    public void hideMessage(int size) {
        if (size != 0) {
            noItemsMessageTV.setVisibility(View.INVISIBLE);
        }
    }

    public void sort() {
        FloatingActionButton sortButton = view.findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sort) {
                    sort = false;
                    stockItemAdapter.sortByTitleAsc();
                } else {
                    sort = true;
                    stockItemAdapter.sortByDesc();
                }
            }
        });
    }

    public void search() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.search_by_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        Spinner search_by_spinner = view.findViewById(R.id.search_by_spinner);
        search_by_spinner.setAdapter(spinnerArrayAdapter);

        EditText search_text_et = view.findViewById(R.id.search_et);
        search_text_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                String selectedSearchValue = search_by_spinner.getSelectedItem().toString();

                if (selectedSearchValue.equals("Category")) {
                    filter(editable.toString(), "Category");
                } else if (selectedSearchValue.equals("Manufacturer")) {
                    filter(editable.toString(), "Manufacturer");
                } else {
                    filter(editable.toString(), "Title");
                }
            }
        });
    }

    private void filter(String text, String type) {
        ArrayList<StockItem> filteredList = new ArrayList<>();

        for (StockItem item : stockItems) {
            filterBy(text, type, item, filteredList);
        }

        stockItemAdapter.filteredList(filteredList);
    }

    private ArrayList<StockItem> filterBy(String text, String type, StockItem item, ArrayList<StockItem> filteredList) {
        if (type.equals("Category")) {
            if (item.getCategory().toLowerCase().contains((text.toLowerCase())))
                filteredList.add(item);
        } else if (type.equals("Manufacturer")) {
            if (item.getManufacturer().toLowerCase().contains((text.toLowerCase())))
                filteredList.add(item);
        } else {
            if (item.getTitle().toLowerCase().contains((text.toLowerCase())))
                filteredList.add(item);
        }

        return filteredList;
    }
}