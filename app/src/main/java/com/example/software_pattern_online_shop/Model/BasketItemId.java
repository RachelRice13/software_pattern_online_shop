package com.example.software_pattern_online_shop.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BasketItemId {
    @Exclude
    public String basketItemId;

    public <T extends BasketItemId> T withId(@NonNull final String id) {
        this.basketItemId = id;
        return (T) this;
    }
}
