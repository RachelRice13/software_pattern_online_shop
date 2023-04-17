package com.example.software_pattern_online_shop.Basket;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.software_pattern_online_shop.HomePage.CustomerHomeFragment;
import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.Model.Purchase;
import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.Model.User;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PaymentFragment extends Fragment {
    private static final String TAG = "PaymentFragment";
    private View view;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userId;
    private Purchase purchase;
    private PaymentMethod paymentMethod;
    private double totalBeforeDiscount, discountAmountNum;
    private EditText cardNumberEt, cardholdersNameEt, expiryDateMonthEt, expiryDateYearEt, securityCodeEt;

    public PaymentFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        cardNumberEt = view.findViewById(R.id.card_number_et);
        cardholdersNameEt = view.findViewById(R.id.cardholders_name_et);
        expiryDateMonthEt = view.findViewById(R.id.expiry_date_month_et);
        expiryDateYearEt = view.findViewById(R.id.expiry_date_year_et);
        securityCodeEt = view.findViewById(R.id.security_code_et);

        Bundle bundle = getArguments();
        purchase = (Purchase) bundle.getSerializable("purchase");
        totalBeforeDiscount = bundle.getDouble("totalBeforeDiscount");
        discountAmountNum = bundle.getDouble("discountAmount");
        paymentMethod = purchase.getPaymentMethod();

        getPurchaseDetails();
        getCustomerPaymentDetails();

        Button checkoutBasketButton = view.findViewById(R.id.checkout_basket_button);
        checkoutBasketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkout();
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, new CustomerHomeFragment()).commit();
                Snackbar.make(view, "Cancelled payment", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void getPurchaseDetails() {
        TextView subtotalTv, discountAmountTv, totalPriceTv;
        subtotalTv = view.findViewById(R.id.subtotal_price_tv);
        discountAmountTv = view.findViewById(R.id.discount_amount_tv);
        totalPriceTv = view.findViewById(R.id.total_price_tv);

        subtotalTv.setText("Subtotal: €" + totalBeforeDiscount);
        discountAmountTv.setText("Discount Amount: " + discountAmountNum + "%");
        totalPriceTv.setText("Total Price: €" + purchase.getTotalPrice());
    }

    private void getCustomerPaymentDetails() {
        firebaseFirestore.collection("customer").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User customer = task.getResult().toObject(User.class);
                            PaymentMethod paymentMethod = customer.getPaymentMethod();

                            cardNumberEt.setText(paymentMethod.getCardNumber());
                            cardholdersNameEt.setText(paymentMethod.getCardholderName());
                            String expiryDate = paymentMethod.getExpiryDate();
                            expiryDateMonthEt.setText(expiryDate.substring(0,2));
                            expiryDateYearEt.setText(expiryDate.substring(3,5));
                            securityCodeEt.setText(paymentMethod.getSecurityCode());
                        }
                    }
                });
    }

    private void checkout() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        String cardNumber = cardNumberEt.getText().toString();
        String cardholderName = cardholdersNameEt.getText().toString();
        String expiryDate =  expiryDateMonthEt.getText().toString() + "/" + expiryDateYearEt.getText().toString();
        String securityCode = securityCodeEt.getText().toString();

        paymentMethod = new PaymentMethod(cardNumber, cardholderName, expiryDate, securityCode);
        purchase.setPaymentMethod(paymentMethod);

        addToFirestore(purchase);
        for (BasketItem basketItem : purchase.getItems()) {
            updateQuantity(basketItem.getItemTitle(), basketItem.getQuantity());
            deleteFromFireStore(basketItem.basketItemId);
        }
        Snackbar.make(view, "Successfully bought items", Snackbar.LENGTH_LONG).show();
        transaction.replace(R.id.container, new CustomerHomeFragment()).commit();
    }

    private void addToFirestore(Purchase purchase) {
        firebaseFirestore.collection("purchase_history").document(userId).collection("purchases")
                .add(purchase)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference reference) {
                        Log.d(TAG, "Added Purchase to DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to Add Purchase to DB", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }

    private void updateQuantity(String title, int purchaseQuantity) {
        firebaseFirestore.collection("items").whereEqualTo("title", title).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())  {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                StockItem stockItem = documentSnapshot.toObject(StockItem.class);
                                int currentQuantity = stockItem.getQuantity();
                                int newQuantity = currentQuantity - purchaseQuantity;
                                String itemId = documentSnapshot.getId();
                                updateItemQuantity(itemId, newQuantity);
                            }
                        }
                    }
                });
    }

    private void updateItemQuantity(String itemId, int newQuantity) {
        firebaseFirestore.collection("items").document(itemId).update("quantity", newQuantity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated item quantity");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update item quantity");
                    }
                });
    }

    private void deleteFromFireStore(String itemId) {
        firebaseFirestore.collection("customer").document(userId).collection("basket").document(itemId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Basket Item successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting basket item", e);
                    }
                });
    }
}