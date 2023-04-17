package com.example.software_pattern_online_shop.Profile;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.Model.User;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private View view;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ImageView profilePicIv;
    private Uri profileUri;
    private User customer;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        profilePicIv = view.findViewById(R.id.profile_pic);

        getUserDetails();

        profilePicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });

        return view;
    }

    private void getUserDetails() {
        TextView fullNameTv = view.findViewById(R.id.profile_user_full_name_tv);
        TextView emailTv = view.findViewById(R.id.profile_email_tv);
        TextView passwordTv = view.findViewById(R.id.profile_password_tv);
        TextView paymentMethodTv = view.findViewById(R.id.profile_payment_method_tv);
        TextView addressTv = view.findViewById(R.id.profile_address_tv);

        firebaseFirestore.collection("customer").document(currentUser.getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            customer = documentSnapshot.toObject(User.class);
                            PaymentMethod paymentMethod = customer.getPaymentMethod();
                            String lastThreeDigits = paymentMethod.getCardNumber().substring(12,15);

                            fullNameTv.setText(customer.getFirstName() + " " + customer.getSurname());
                            emailTv.setText(customer.getEmail());
                            passwordTv.setText(customer.getPassword());
                            paymentMethodTv.setText("Card ending with: " + lastThreeDigits);
                            addressTv.setText(customer.getAddress());

                            if (customer.getProfileImagePath().equals("")) {
                                profilePicIv.setImageResource(R.drawable.ic_profile);
                            } else {
                                StorageReference profileImageRef = storageReference.child(customer.getProfileImagePath());
                                profileImageRef.getBytes(1024*1024)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                profilePicIv.setImageBitmap(bitmap);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void chooseProfilePicture() {
        Intent choosePictureIntent = new Intent();
        choosePictureIntent.setType("image/*");
        choosePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(choosePictureIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            profileUri = data.getData();
            profilePicIv.setImageURI(profileUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image....");
        progressDialog.show();

        StorageReference profilePictureRef = storageReference.child("users/" + currentUser.getUid() + "/profile_pic");

        profilePictureRef.putFile(profileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Snackbar.make(view, "Image Uploaded", Snackbar.LENGTH_LONG).show();
                        updateProfileImagePath(profilePictureRef.getPath());
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to Upload Picture", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
                    }
                });
    }

    private void updateProfileImagePath(String imagePath) {
        firebaseFirestore.collection("customer").document(currentUser.getUid()).update("profileImagePath", imagePath)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated image path in firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update image path.", e);
                    }
                });
    }
}