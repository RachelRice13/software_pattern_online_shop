package com.example.software_pattern_online_shop.HomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.software_pattern_online_shop.Basket.BasketFragment;
import com.example.software_pattern_online_shop.MainActivity;
import com.example.software_pattern_online_shop.Profile.ProfileFragment;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerHomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        firebaseAuth = FirebaseAuth.getInstance();
        buildMenu();

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void buildMenu() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new CustomerHomeFragment()).commit();
        MaterialToolbar toolbar = findViewById(R.id.customer_top_app_bar);
        drawerLayout = findViewById(R.id.customer_drawer_layout);
        NavigationView navigationView = findViewById(R.id.customer_navigation_view);
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
                    case R.id.nav_customer_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new CustomerHomeFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_customer_basket:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new BasketFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_customer_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
                        drawerLayout.close();
                        return true;
                    case R.id.nav_customer_logout:
                        logout();
                        return true;
                }
                return false;
            }
        });
    }

    private void logout() {
        AlertDialog.Builder logoutAD = new AlertDialog.Builder(CustomerHomeActivity.this);

        logoutAD
                .setCancelable(false)
                .setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        Intent logoutIntent = new Intent(CustomerHomeActivity.this, MainActivity.class);
                        startActivity(logoutIntent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        drawerLayout.close();
                    }
                });

        AlertDialog alertDialog = logoutAD.create();
        alertDialog.show();
    }
}