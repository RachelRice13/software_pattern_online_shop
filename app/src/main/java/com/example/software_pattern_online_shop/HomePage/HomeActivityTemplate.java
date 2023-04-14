package com.example.software_pattern_online_shop.HomePage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.software_pattern_online_shop.MainActivity;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public abstract class HomeActivityTemplate extends AppCompatActivity {

    public abstract void buildMenu();

    public void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void setUpMenu(Fragment fragment, DrawerLayout drawerLayout) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout.bringToFront();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    public boolean replaceFragment(MenuItem item, int id, Fragment fragment, DrawerLayout drawerLayout) {
        if (item.getItemId() == id) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            drawerLayout.close();
            return true;
        }
        return false;
    }

    public void logout(Activity activity, FirebaseAuth firebaseAuth, DrawerLayout drawerLayout) {
        AlertDialog.Builder logoutAD = new AlertDialog.Builder(activity);

        logoutAD
                .setCancelable(false)
                .setTitle("Do you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        Intent logoutIntent = new Intent(activity, MainActivity.class);
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