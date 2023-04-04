package com.example.software_pattern_online_shop.Model;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class StockItemId {
    @Exclude
    public String StockItemId;

    public <T extends StockItemId> T withId(@NonNull final String id) {
        this.StockItemId = id;
        return (T) this;
    }
}
