package com.example.software_pattern_online_shop.LoginRegister;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.software_pattern_online_shop.Common.Validation;
import com.example.software_pattern_online_shop.HomePage.AdminHomeActivity;
import com.example.software_pattern_online_shop.Model.Admin;
import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminRegisterActivity extends RegisterUserTemplate {
    private static final String TAG = "AdminRegisterActivity";
    private TextInputLayout jobTitleLO, employeeNumLO;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_register);

        firebaseAuth = FirebaseAuth.getInstance();
        jobTitleLO = findViewById(R.id.reg_job_title);
        employeeNumLO = findViewById(R.id.reg_employee_number);

        buttonSetup();
        hideActionBar();
    }

    @Override
    public void register() {
        EditText jobTitleET, employeeNumET;
        jobTitleET = findViewById(R.id.reg_job_title_et);
        employeeNumET = findViewById(R.id.reg_employee_number_et);

        String jobTitle = jobTitleET.getText().toString();
        String employeeNum = employeeNumET.getText().toString();

        boolean validJobTitle = Validation.validateBlank(jobTitle, jobTitleLO);
        boolean validEmployeeNum = Validation.validateEmployeeNumber(employeeNum, employeeNumLO);

        if (validJobTitle && validEmployeeNum) {
            addUserToDB(jobTitle, employeeNum, null);
        }
    }

    @Override
    public void addUserToDB(String jtOrA, String employeeNum, PaymentMethod paymentMethod) {
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String surname = getIntent().getStringExtra("SURNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Admin admin = new Admin(firstName, surname, email, password, jtOrA, employeeNum);
                            String adminId = FirebaseAuth.getInstance().getUid();;
                            addUserToFireStore("admin", adminId, admin, AdminRegisterActivity.this, AdminHomeActivity.class);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                        }
                    }
                });
    }
}