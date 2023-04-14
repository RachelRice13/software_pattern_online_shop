package com.example.software_pattern_online_shop.HomePage;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.software_pattern_online_shop.R;

public class CustomerHomeFragment extends HomeFragmentTemplate {

    public CustomerHomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);

        createHomePage(view);

        return view;
    }
}