package com.example.software_pattern_online_shop.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.software_pattern_online_shop.Model.Customer;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private View view;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getUserDetails();

        return view;
    }

    private void getUserDetails() {
        TextView fullNameTv = view.findViewById(R.id.profile_user_full_name_tv);
        TextView emailTv = view.findViewById(R.id.profile_email_tv);
        TextView passwordTv = view.findViewById(R.id.profile_password_tv);
        TextView paymentMethodTv = view.findViewById(R.id.profile_payment_method_tv);
        TextView addressTv = view.findViewById(R.id.profile_address_tv);
        ImageView profilePicIv = view.findViewById(R.id.profile_pic);

        firebaseFirestore.collection("customer").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Customer customer = documentSnapshot.toObject(Customer.class);

                            fullNameTv.setText(customer.getFirstName() + " " + customer.getSurname());
                            emailTv.setText(customer.getEmail());
                            passwordTv.setText(customer.getPassword());
                            paymentMethodTv.setText(customer.getPaymentMethod());
                            addressTv.setText(customer.getAddress());
                        }
                    }
                });
    }
}