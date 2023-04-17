package com.example.software_pattern_online_shop.LoginRegister;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.software_pattern_online_shop.Common.Validation;
import com.example.software_pattern_online_shop.HomePage.CustomerHomeActivity;
import com.example.software_pattern_online_shop.Model.Customer;
import com.example.software_pattern_online_shop.Model.PaymentMethod;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerRegisterActivity extends RegisterUserTemplate {
    private static final String TAG = "CustomerRegActivity";
    private TextInputLayout addressLine1LO, townOrCityLO, eircodeLO;
    private Spinner countySpinner;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        firebaseAuth = FirebaseAuth.getInstance();
        addressLine1LO = findViewById(R.id.reg_address_line_1);
        townOrCityLO = findViewById(R.id.reg_town_or_city);
        eircodeLO = findViewById(R.id.reg_eircode);
        countySpinner = findViewById(R.id.reg_county_spinner);

        setCountySpinner(countySpinner);
        buttonSetup();
        hideActionBar();
    }

    @Override
    public void register() {
        EditText addressL1ET, addressL2ET, addressL3ET, townOrCityET, eircodeET, cardNumberET, cardholdersNameET, expiryMonthET, expiryYearET, securityCodeET;
        addressL1ET = findViewById(R.id.reg_address_line_1_et);
        addressL2ET = findViewById(R.id.reg_address_line_2_et);
        addressL3ET = findViewById(R.id.reg_address_line_3_et);
        townOrCityET = findViewById(R.id.reg_town_or_city_et);
        eircodeET = findViewById(R.id.reg_eircode_et);
        cardNumberET = findViewById(R.id.reg_card_number_et);
        cardholdersNameET = findViewById(R.id.reg_cardholder_name_et);
        expiryMonthET = findViewById(R.id.expiry_date_month_et);
        expiryYearET = findViewById(R.id.expiry_date_year_et);
        securityCodeET = findViewById(R.id.security_code_et);

        String addressLine1 = addressL1ET.getText().toString();
        String addressLine2 = addressL2ET.getText().toString();
        String addressLine3 = addressL3ET.getText().toString();
        String townOrCity = townOrCityET.getText().toString();
        String county = countySpinner.getSelectedItem().toString();
        String eircode = eircodeET.getText().toString();

        String cardNumber = cardNumberET.getText().toString();
        String cardholdersName = cardholdersNameET.getText().toString();
        String expiryDate = expiryMonthET.getText().toString() + "/" + expiryYearET.getText().toString();
        String securityCode = securityCodeET.getText().toString();
        PaymentMethod paymentMethod = new PaymentMethod(cardNumber, cardholdersName, expiryDate, securityCode);

        boolean validAddressLine1 = Validation.validateBlank(addressLine1, addressLine1LO);
        boolean validTownOrCity = Validation.validateBlank(townOrCity, townOrCityLO);
        boolean validCounty = Validation.validateCounty(county, this);
        boolean validEircode = Validation.validateEircode(eircode, eircodeLO);

        if (validAddressLine1 && validTownOrCity && validCounty && validEircode) {
            String address = addressLine1 + ", " + addressLine2 + ", " + addressLine3 + ", " + townOrCity + ", " + county + ", " + eircode;
            addUserToDB(address, null, paymentMethod);
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
                            Customer customer = new Customer(firstName, surname, email, password, jtOrA, paymentMethod);
                            String customerId = FirebaseAuth.getInstance().getUid();;
                            addUserToFireStore("customer", customerId, customer, CustomerRegisterActivity.this, CustomerHomeActivity.class);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    private void setCountySpinner(Spinner countySpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.county_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countySpinner.setAdapter(spinnerArrayAdapter);
    }
}