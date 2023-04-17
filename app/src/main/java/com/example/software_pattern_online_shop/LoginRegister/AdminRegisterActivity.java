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

import com.example.software_pattern_online_shop.Common.Validation;
import com.example.software_pattern_online_shop.HomePage.AdminHomeActivity;
import com.example.software_pattern_online_shop.Model.Admin;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminRegisterActivity extends AppCompatActivity {
    private static final String TAG = "AdminRegisterActivity";
    private TextInputLayout jobTitleLO, employeeNumLO;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        jobTitleLO = findViewById(R.id.reg_job_title);
        employeeNumLO = findViewById(R.id.reg_employee_number);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        Button goBack = findViewById(R.id.go_back_button);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void register() {
        EditText jobTitleET, employeeNumET;
        jobTitleET = findViewById(R.id.reg_job_title_et);
        employeeNumET = findViewById(R.id.reg_employee_number_et);

        String jobTitle = jobTitleET.getText().toString();
        String employeeNum = employeeNumET.getText().toString();

        boolean validJobTitle = Validation.validateBlank(jobTitle, jobTitleLO);
        boolean validEmployeeNum = Validation.validateEmployeeNumber(employeeNum, employeeNumLO);

        if (validJobTitle && validEmployeeNum) {
            addAdminToDB(jobTitle, employeeNum);
        }
    }

    private void addAdminToDB(String jobTitle, String employeeNum) {
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String surname = getIntent().getStringExtra("SURNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Admin admin = new Admin(firstName, surname, email, password, jobTitle, employeeNum);
                            String adminId = FirebaseAuth.getInstance().getUid();;
                            addAdminToFireStore(admin, adminId);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    private void addAdminToFireStore(Admin admin, String adminId) {
        firebaseFirestore.collection("admin").document(adminId)
                .set(admin)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "createUserWithEmail: successful");
                        Intent adminRegisterIntent = new Intent(AdminRegisterActivity.this, AdminHomeActivity.class);
                        startActivity(adminRegisterIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminRegisterActivity.this, "Failed to Register Admin", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }
}