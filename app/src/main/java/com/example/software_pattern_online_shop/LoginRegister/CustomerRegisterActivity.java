package com.example.software_pattern_online_shop.LoginRegister;

import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private LinearLayout cardDetailsLl;
    private RelativeLayout addressRl;
    private TextInputLayout addressLine1LO, townOrCityLO, eircodeLO, cardNumberLO, cardHoldersNameLO;
    private String addressLine1, townOrCity, county, eircode, cardNumber, cardholdersName, expiryDate, securityCode;
    private Spinner countySpinner;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        firebaseAuth = FirebaseAuth.getInstance();
        cardDetailsLl = findViewById(R.id.card_details_ll);
        addressRl = findViewById(R.id.address_details_rl);
        addressLine1LO = findViewById(R.id.reg_address_line_1);
        townOrCityLO = findViewById(R.id.reg_town_or_city);
        eircodeLO = findViewById(R.id.reg_eircode);
        cardNumberLO = findViewById(R.id.reg_card_number);
        cardHoldersNameLO = findViewById(R.id.reg_cardholder_name);
        countySpinner = findViewById(R.id.reg_county_spinner);

        setCountySpinner(countySpinner);
        buttonSetup();
        switchLayoutButtons();
        hideActionBar();
    }

    @Override
    public void register() {
        getDetails();
        PaymentMethod paymentMethod = new PaymentMethod(cardNumber, cardholdersName, expiryDate, securityCode);

        boolean validAddressLine1 = Validation.validateBlank(addressLine1, addressLine1LO);
        boolean validTownOrCity = Validation.validateBlank(townOrCity, townOrCityLO);
        boolean validCounty = Validation.validateCounty(county, this);
        boolean validEircode = Validation.validateEircode(eircode, eircodeLO);
        boolean validCardNumber = Validation.validateCardNumber(cardNumber, cardNumberLO);
        boolean validCardHoldersName = Validation.validateBlank(cardholdersName, cardHoldersNameLO);
        boolean validExpiryDate = Validation.validateExpiryDate(expiryDate, this);
        boolean validSecurityCode = Validation.validateSecurityNumber(securityCode, this);

        if (validAddressLine1 && validTownOrCity && validCounty && validEircode && validCardNumber && validCardHoldersName && validExpiryDate && validSecurityCode) {
            String address = addressLine1 + ", "  + townOrCity + ", " + county + ", " + eircode;
            addUserToDB(address, null, paymentMethod);
        }
    }

    public void getDetails() {
        EditText addressL1ET, townOrCityET, eircodeET, cardNumberET, cardholdersNameET, expiryMonthET, expiryYearET, securityCodeET;
        addressL1ET = findViewById(R.id.reg_address_line_1_et);
        townOrCityET = findViewById(R.id.reg_town_or_city_et);
        eircodeET = findViewById(R.id.reg_eircode_et);
        cardNumberET = findViewById(R.id.reg_card_number_et);
        cardholdersNameET = findViewById(R.id.reg_cardholder_name_et);
        expiryMonthET = findViewById(R.id.expiry_date_month_et);
        expiryYearET = findViewById(R.id.expiry_date_year_et);
        securityCodeET = findViewById(R.id.security_code_et);

        addressLine1 = addressL1ET.getText().toString();
        townOrCity = townOrCityET.getText().toString();
        county = countySpinner.getSelectedItem().toString();
        eircode = eircodeET.getText().toString();

        cardNumber = cardNumberET.getText().toString();
        cardholdersName = cardholdersNameET.getText().toString();
        expiryDate = expiryMonthET.getText().toString() + "/" + expiryYearET.getText().toString();
        securityCode = securityCodeET.getText().toString();
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

    private void switchLayoutButtons() {
        Button paymentDetailsBut, addressBut;
        paymentDetailsBut = findViewById(R.id.payment_details_button);
        addressBut = findViewById(R.id.address_button);

        paymentDetailsBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardDetailsLl.setVisibility(View.VISIBLE);
                addressRl.setVisibility(View.INVISIBLE);
            }
        });

        addressBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardDetailsLl.setVisibility(View.INVISIBLE);
                addressRl.setVisibility(View.VISIBLE);
            }
        });
    }
}