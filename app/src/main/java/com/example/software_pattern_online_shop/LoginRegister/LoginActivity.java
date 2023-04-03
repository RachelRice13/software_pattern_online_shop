package com.example.software_pattern_online_shop.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.software_pattern_online_shop.HomePage.AdminHomeActivity;
import com.example.software_pattern_online_shop.HomePage.CustomerHomeActivity;
import com.example.software_pattern_online_shop.MainActivity;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout emailLO, passwordLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        emailLO = findViewById(R.id.login_email);
        passwordLO = findViewById(R.id.login_password);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        Button goToLP = findViewById(R.id.landing_page_button);
        goToLP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landingPageIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(landingPageIntent);
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void login() {
        EditText emailET, passwordET;
        emailET = findViewById(R.id.login_email_et);
        passwordET = findViewById(R.id.login_password_et);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean validEmail = RegisterActivity.validateBlank(email, emailLO);
        boolean validPassword = RegisterActivity.validateBlank(password, passwordLO);

        if (validEmail && validPassword) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "loginUserWithEmail: success");
                                goToHomePage();
                            } else {
                                Log.e(TAG, "loginUserWithEmail: failure", task.getException());
                                if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    emailLO.setError("Email doesn't exist");
                                }
                                if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                    passwordLO.setError("Password doesn't match");
                                }
                            }
                        }
                    });
        }
    }

    private void goToHomePage() {
        firebaseFirestore.collection("admin").document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Intent adminIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            startActivity(adminIntent);
                        } else {
                            firebaseFirestore.collection("customer").document(firebaseAuth.getCurrentUser().getUid()).get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (documentSnapshot.exists()) {
                                                Intent customerIntent = new Intent(LoginActivity.this, CustomerHomeActivity.class);
                                                startActivity(customerIntent);
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Error Logging In", Toast.LENGTH_SHORT).show();
                                                Log.e(TAG, "User doesn't exist in either the Admin or Customer collections");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}