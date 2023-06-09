package com.example.software_pattern_online_shop.LoginRegister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.software_pattern_online_shop.Common.Validation;
import com.example.software_pattern_online_shop.MainActivity;
import com.example.software_pattern_online_shop.R;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private static final String FIRST_NAME = "FIRST_NAME";
    private static final String SURNAME = "SURNAME";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private RadioGroup radioGroup;
    private String accountType = "";
    private TextInputLayout firstNameLO, surnameLO, emailLO, passwordLO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        radioGroup = findViewById(R.id.radio_group);
        firstNameLO = findViewById(R.id.reg_first_name);
        surnameLO = findViewById(R.id.reg_surname);
        emailLO = findViewById(R.id.reg_email);
        passwordLO = findViewById(R.id.reg_password);

        Button continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTo();
            }
        });

        Button goToLP = findViewById(R.id.landing_page_button);
        goToLP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent landingPageIntent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(landingPageIntent);
            }
        });

        // Hides the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void continueTo() {
        EditText firstNameET, surnameET, emailET, passwordET;
        firstNameET = findViewById(R.id.reg_first_name_et);
        surnameET = findViewById(R.id.reg_surname_et);
        emailET = findViewById(R.id.reg_email_et);
        passwordET = findViewById(R.id.reg_password_et);

        String firstName = firstNameET.getText().toString();
        String surname = surnameET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        boolean validFirstName = Validation.validateBlank(firstName, firstNameLO);
        boolean validSurname = Validation.validateBlank(surname, surnameLO);
        boolean validEmail = Validation.validateBlank(email, emailLO);
        boolean validPassword = Validation.validatePassword(password, passwordLO);

        getSelectedRB();

        if (validFirstName && validSurname && validEmail && validPassword && !accountType.equals("")) {
            if (accountType.equals("Admin")) {
                Intent adminIntent = new Intent(RegisterActivity.this, AdminRegisterActivity.class);
                passData(adminIntent, firstName, surname, email, password);
                startActivity(adminIntent);
            } else if (accountType.equals("Customer")) {
                Intent customerIntent = new Intent(RegisterActivity.this, CustomerRegisterActivity.class);
                passData(customerIntent, firstName, surname, email, password);
                startActivity(customerIntent);
            }
        }

    }

    private void getSelectedRB() {
        int selectedButton = radioGroup.getCheckedRadioButtonId();
        if (selectedButton == -1) {
            Toast.makeText(this, "Nothing Selected", Toast.LENGTH_SHORT).show();
        } else if (selectedButton == R.id.radio_admin){
            accountType = "Admin";
        } else if (selectedButton == R.id.radio_customer){
            accountType = "Customer";
        }
    }

    private void passData(Intent intent, String firstName, String surname, String email, String password) {
        intent.putExtra(FIRST_NAME, firstName);
        intent.putExtra(SURNAME, surname);
        intent.putExtra(EMAIL, email);
        intent.putExtra(PASSWORD, password);
    }
}