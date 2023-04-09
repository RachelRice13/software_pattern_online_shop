package com.example.software_pattern_online_shop.Basket;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.software_pattern_online_shop.HomePage.AdminHomeFragment;
import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.Model.Purchase;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BasketFragment extends Fragment {
    private static final String TAG = "BasketFragment";
    private View view;
    private FirebaseFirestore firebaseFirestore;
    private ListenerRegistration listenerRegistration;
    private ArrayList<BasketItem> basket;
    private BasketAdapter basketAdapter;
    private String userId;
    private double totalPrice, totalBeforeDiscount, discountAmountNum;
    private int totalQuantity;
    private TextView totalPriceTv, totalQuantityTv, discountAppliedTv;

    public BasketFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_basket, container, false);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        totalPriceTv = view.findViewById(R.id.total_price_tv);
        totalQuantityTv = view.findViewById(R.id.total_quantity_tv);
        discountAppliedTv = view.findViewById(R.id.discount_applied_tv);
        buildRecyclerView();

        Button securePaymentButton = view.findViewById(R.id.checkout_basket_button);
        securePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

        Button continueShoppingBut = view.findViewById(R.id.continue_shopping_button);
        continueShoppingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction.replace(R.id.container, new AdminHomeFragment()).commit();
            }
        });

        Button applyDiscountButton = view.findViewById(R.id.apply_discount_code_button);
        applyDiscountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyDiscountCode();
            }
        });

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
                        double itemPrice = basketItem.getPrice() * basketItem.getQuantity();
                        totalPrice += itemPrice;
                        totalBeforeDiscount = totalPrice;
                        totalQuantity += basketItem.getQuantity();
                        basketAdapter.notifyDataSetChanged();
                        totalPriceTv.setText("Shopping Basket Total €" + totalPrice);
                        totalQuantityTv.setText(totalQuantity + " Items");
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

    private void checkout() {
        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Purchase purchase = new Purchase(basket, totalPrice, date, new PaymentMethod());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        if (!basket.isEmpty()) {
            PaymentFragment paymentFragment = new PaymentFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("purchase", purchase);
            bundle.putDouble("totalBeforeDiscount", totalBeforeDiscount);
            bundle.putDouble("discountAmount", discountAmountNum);
            paymentFragment.setArguments(bundle);
            transaction.replace(R.id.container, paymentFragment).commit();
        } else {
            Snackbar.make(view, "Basket is empty", Snackbar.LENGTH_LONG).show();
        }
    }

    private void applyDiscountCode() {
        EditText discountCodeEt = view.findViewById(R.id.discount_code_et);
        String discountCode = discountCodeEt.getText().toString();

        boolean tenPercDiscount = applyDiscount(discountCode, "discount10", 0.1);
        boolean fifteenPercDiscount = applyDiscount(discountCode, "discount15", 0.15);
        boolean twentyPercDiscount = applyDiscount(discountCode, "discount20", 0.2);

        if (!tenPercDiscount && !fifteenPercDiscount && !twentyPercDiscount) {
            Snackbar.make(view, "Invalid discount code!", Snackbar.LENGTH_LONG).show();
        }

        totalPriceTv.setText("Shopping Basket Total €" + totalPrice);
        discountCodeEt.setText("");

    }

    private boolean applyDiscount(String enteredCode, String discountCode, double discountAmount) {
        if (enteredCode.equals(discountCode)) {
            discountAmountNum = discountAmount * 100;
            totalPrice = totalPrice - (totalPrice*discountAmount);
            discountAppliedTv.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }
}