package com.example.software_pattern_online_shop.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.software_pattern_online_shop.Customer.CustomerDetailsFragment;
import com.example.software_pattern_online_shop.R;
import com.example.software_pattern_online_shop.Stock.AddNewStockFragment;
import com.example.software_pattern_online_shop.Stock.UpdateStockFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AdminHomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        buildMenu();

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void buildMenu() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AdminHomeFragment()).commit();
        MaterialToolbar toolbar = findViewById(R.id.admin_top_app_bar);
        drawerLayout = findViewById(R.id.admin_drawer_layout);
        NavigationView navigationView = findViewById(R.id.admin_navigation_view);
        drawerLayout.bringToFront();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_admin_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AdminHomeFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_admin_add_new_stock:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddNewStockFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_admin_update_stock:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new UpdateStockFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_admin_customer_details:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new CustomerDetailsFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_customer_logout:
                        return true;
                }
                return false;
            }
        });
    }
}