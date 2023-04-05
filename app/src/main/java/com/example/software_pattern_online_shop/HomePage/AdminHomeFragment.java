package com.example.software_pattern_online_shop.HomePage;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AdminHomeFragment extends Fragment {
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<StockItem> stockItems;
    private StockItemAdapter stockItemAdapter;
    private TextView noItemsMessageTV;
    private Spinner search_by_spinner;

   public AdminHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_admin_home, container, false);

       firebaseFirestore = FirebaseFirestore.getInstance();
       noItemsMessageTV = view.findViewById(R.id.no_items_message_tv);
       search_by_spinner = view.findViewById(R.id.search_by_spinner);
       buildRecyclerView();
       search();

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
        RecyclerView recyclerView = view.findViewById(R.id.stock_items_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        stockItemAdapter = new StockItemAdapter(stockItems, getContext(), getFragmentManager());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(stockItemAdapter);
        populateList();
    }

    private void search() {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.search_by_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
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

    protected void filter(String text, String type) {
       ArrayList<StockItem> filteredList = new ArrayList<>();

        for (StockItem item : stockItems) {
            filterBy(text, type, item, filteredList);
        }

        stockItemAdapter.filteredList(filteredList);
    }

    private ArrayList<StockItem> filterBy(String text, String type, StockItem item, ArrayList<StockItem> filteredList) {
       if (type.equals("Category")) {
           if (item.getCategory().toLowerCase().contains((text.toLowerCase()))) {
               filteredList.add(item);
           }
       } else if (type.equals("Manufacturer")) {
           if (item.getManufacturer().toLowerCase().contains((text.toLowerCase()))) {
               filteredList.add(item);
           }
       } else {
           if (item.getTitle().toLowerCase().contains((text.toLowerCase()))) {
               filteredList.add(item);
           }
       }

       return filteredList;
    }
}