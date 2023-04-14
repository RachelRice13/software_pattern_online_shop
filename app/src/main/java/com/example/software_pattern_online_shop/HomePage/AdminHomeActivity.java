package com.example.software_pattern_online_shop.HomePage;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.software_pattern_online_shop.Customer.CustomerDetailsFragment;
import com.example.software_pattern_online_shop.R;
import com.example.software_pattern_online_shop.Stock.AddNewStockFragment;
import com.example.software_pattern_online_shop.Stock.UpdateStockFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends HomeActivityTemplate {
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

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
                replaceFragment(item, R.id.nav_admin_home, new AdminHomeFragment(), drawerLayout);
                replaceFragment(item, R.id.nav_admin_add_new_stock, new AddNewStockFragment(), drawerLayout);
                replaceFragment(item, R.id.nav_admin_update_stock, new UpdateStockFragment(), drawerLayout);
                replaceFragment(item, R.id.nav_admin_customer_details, new CustomerDetailsFragment(), drawerLayout);
                if (item.getItemId() == R.id.nav_admin_logout) {
                    logout(AdminHomeActivity.this, firebaseAuth, drawerLayout);
                    return true;
                }
                return false;
            }
        });
    }
}