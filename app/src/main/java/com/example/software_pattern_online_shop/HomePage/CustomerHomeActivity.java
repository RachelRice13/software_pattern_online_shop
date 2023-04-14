package com.example.software_pattern_online_shop.HomePage;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.software_pattern_online_shop.Basket.BasketFragment;
import com.example.software_pattern_online_shop.Profile.ProfileFragment;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHomeActivity extends HomeActivityTemplate {
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        firebaseAuth = FirebaseAuth.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);

        buildMenu();
        hideActionBar();
    }


    @Override
    public void buildMenu() {
        setUpMenu(new CustomerHomeFragment(), drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                replaceFragment(item, R.id.nav_customer_home, new CustomerHomeFragment(), drawerLayout);
                replaceFragment(item, R.id.nav_customer_basket, new BasketFragment(), drawerLayout);
                replaceFragment(item, R.id.nav_customer_profile, new ProfileFragment(), drawerLayout);
                if (item.getItemId() == R.id.nav_customer_logout) {
                    logout(CustomerHomeActivity.this, firebaseAuth, drawerLayout);
                    return true;
                }
                return false;
            }
        });
    }
}