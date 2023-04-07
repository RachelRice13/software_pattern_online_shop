package com.example.software_pattern_online_shop.Stock;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.software_pattern_online_shop.HomePage.AdminHomeFragment;
import com.example.software_pattern_online_shop.Model.Admin;
import com.example.software_pattern_online_shop.Model.BasketItem;
import com.example.software_pattern_online_shop.Model.Comment;
import com.example.software_pattern_online_shop.Model.Customer;
import com.example.software_pattern_online_shop.Model.Rating;
import com.example.software_pattern_online_shop.Model.StockItem;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewStockItemFragment extends Fragment {
    private static final String TAG = "ViewStockItemFragment";
    private View view;
    private StockItem stockItem;
    private StorageReference storageReference;
    private LinearLayout detailsLL, commentsLL, ratingsLL, addToBasketLL;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private int position, quantity = 1;
    private String userId, username;
    private ArrayList<Comment> comments;
    private CommentsAdapter commentsAdapter;

    public ViewStockItemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_stock_item, container, false);

        Bundle bundle = getArguments();
        stockItem = (StockItem) bundle.getSerializable("stockItem");

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        userId = firebaseAuth.getCurrentUser().getUid();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        detailsLL = view.findViewById(R.id.details_ll);
        commentsLL = view.findViewById(R.id.comments_ll);
        ratingsLL = view.findViewById(R.id.ratings_ll);
        addToBasketLL = view.findViewById(R.id.add_to_basket_ll);

        choose();
        getItemDetails();
        leaveComment();
        leaveRating();

        FloatingActionButton goBack = view.findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transaction.replace(R.id.container, new AdminHomeFragment()).commit();
            }
        });

        return view;
    }

    private void getItemDetails() {
        TextView titleTv, manufacturerTv, priceTv, quantityTv, categoryTv;
        ImageView itemImageIv = view.findViewById(R.id.item_image_iv);

        titleTv = view.findViewById(R.id.item_title_tv);
        manufacturerTv = view.findViewById(R.id.item_manufacturer_tv);
        priceTv = view.findViewById(R.id.item_price_tv);
        quantityTv = view.findViewById(R.id.item_quantity_tv);
        categoryTv = view.findViewById(R.id.item_category_tv);

        titleTv.setText(stockItem.getTitle());
        manufacturerTv.setText(stockItem.getManufacturer());
        priceTv.setText(String.valueOf(stockItem.getPrice()));
        quantityTv.setText(String.valueOf(stockItem.getQuantity()));
        categoryTv.setText(stockItem.getCategory());

        StorageReference stockImageRef = storageReference.child(stockItem.getImagePath());
        stockImageRef.getBytes(1024*1024)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        itemImageIv.setImageBitmap(bitmap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        itemImageIv.setImageResource(R.drawable.ic_basket);
                    }
                });

        getUsername();
        basket();
    }

    private void basket() {
        Button increaseQuantityBut, decreaseQuantityBut, addItemToBasketBut;
        EditText quantityEt = view.findViewById(R.id.item_quantity_et);
        increaseQuantityBut = view.findViewById(R.id.increase_quantity_button);
        decreaseQuantityBut = view.findViewById(R.id.decrease_quantity_button);
        addItemToBasketBut = view.findViewById(R.id.add_item_to_basket_button);

        increaseQuantityBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity += 1;
                quantityEt.setText(String.valueOf(quantity));
            }
        });

        decreaseQuantityBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity -= 1;
                quantityEt.setText(String.valueOf(quantity));
            }
        });

        addItemToBasketBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = Integer.parseInt(quantityEt.getText().toString());
                boolean validQuantity = validateQuantity(quantity);

                if (validQuantity) {
                    BasketItem basketItem = new BasketItem(stockItem.getTitle(), stockItem.getImagePath(), stockItem.getPrice(), quantity);
                    addToBasket(basketItem);
                }
            }
        });
    }

    private boolean validateQuantity(int quantity) {
        if (quantity > stockItem.getQuantity()) {
            Toast.makeText(getContext(), "There are only " + stockItem.getQuantity() + " " + stockItem.getTitle() + " remaining.", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void addToBasket(BasketItem basketItem) {
        firebaseFirestore.collection("customer").document(userId).collection("basket")
                .add(basketItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Snackbar.make(view, "Added item to basket", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }

    private void leaveComment() {
        EditText commentEt = view.findViewById(R.id.type_message_et);
        FloatingActionButton sendMessageBut = view.findViewById(R.id.send_message_button);

        getComments();

        sendMessageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = commentEt.getText().toString();
                Comment newComment = new Comment(comment, username);

                if (comment.equals("")) {
                    Toast.makeText(getContext(), "Type a message into the comment box", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseFirestore.collection("items").whereEqualTo("title", stockItem.getTitle()).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                            String docId = documentSnapshot.getId();
                                            addToArrayFireStore(newComment, docId, "comments");
                                            commentsAdapter.notifyDataSetChanged();
                                            commentEt.setText("");
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }

    private void getComments() {
        comments = stockItem.getComments();
        RecyclerView recyclerView = view.findViewById(R.id.comments_rv);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        commentsAdapter = new CommentsAdapter(comments, getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(commentsAdapter);
    }

    private void getUsername() {
        firebaseFirestore.collection("admin").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Admin admin = documentSnapshot.toObject(Admin.class);
                            username = admin.getFirstName() + " " + admin.getSurname();
                            addToBasketLL.setVisibility(View.GONE);
                        } else {
                            firebaseFirestore.collection("customer").document(firebaseAuth.getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Customer customer = documentSnapshot.toObject(Customer.class);
                                                username = customer.getFirstName() + " " + customer.getSurname();
                                            } else {
                                                Log.e(TAG, "User doesn't exist in either the Admin or Customer collections");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void addToArrayFireStore(Object object, String docId, String arrayName) {
        firebaseFirestore.collection("items").document(docId).update(arrayName, FieldValue.arrayUnion(object))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Added comment to DB");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to add comment to DB");
                    }
                });
    }

    private void leaveRating() {
        RatingBar ratingBar = view.findViewById(R.id.item_rating_bar);
        Button leaveRatingBut = view.findViewById(R.id.leave_rating_but);

        setRating(userId, ratingBar);

        leaveRatingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float itemRating = ratingBar.getRating();
                Rating rating = new Rating(itemRating, userId);

                firebaseFirestore.collection("items").whereEqualTo("title", stockItem.getTitle()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                        StockItem item = documentSnapshot.toObject(StockItem.class);
                                        ArrayList<Rating> ratings = item.getRatings();
                                        String docId = documentSnapshot.getId();
                                        boolean found = false;

                                        for (int i = 0; i < ratings.size(); i++) {
                                            Rating r = ratings.get(i);
                                            if (r.getUserId().equals(userId)) {
                                                found = true;
                                                position = i;
                                            }
                                        }

                                        if (found)
                                            updateRatingValue(docId, ratings, position, rating);
                                        else
                                            addToArrayFireStore(rating, docId, "ratings");;

                                    }
                                }
                            }
                        });
            }
        });
    }

    private void setRating(String userId, RatingBar ratingBar) {
        firebaseFirestore.collection("items").whereEqualTo("title", stockItem.getTitle()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                StockItem item = documentSnapshot.toObject(StockItem.class);

                                for (Rating r : item.getRatings()) {
                                    if (r.getUserId().equals(userId)) {
                                        ratingBar.setRating(r.getRating());
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void updateRatingValue(String docId, ArrayList<Rating> ratings, int position, Rating rating) {
        ratings.set(position, rating);

        firebaseFirestore.collection("items").document(docId).update("ratings", ratings)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Updated rating value");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Failed to update rating value");
                    }
                });
    }

    private void choose() {
        Button detailsBut, commentsBut, ratingsBut;
        detailsBut = view.findViewById(R.id.details_button);
        commentsBut = view.findViewById(R.id.comments_button);
        ratingsBut = view.findViewById(R.id.rating_button);

        detailsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLL.setVisibility(View.VISIBLE);
                commentsLL.setVisibility(View.INVISIBLE);
                ratingsLL.setVisibility(View.INVISIBLE);
            }
        });

        commentsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLL.setVisibility(View.INVISIBLE);
                commentsLL.setVisibility(View.VISIBLE);
                ratingsLL.setVisibility(View.INVISIBLE);
            }
        });

        ratingsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detailsLL.setVisibility(View.INVISIBLE);
                commentsLL.setVisibility(View.INVISIBLE);
                ratingsLL.setVisibility(View.VISIBLE);
            }
        });
    }
}