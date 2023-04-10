package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;

public class Rating implements Serializable {
    float rating;
    String userId;

    public Rating(){}

    public Rating(float rating, String userId) {
        this.rating = rating;
        this.userId = userId;
    }

    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
