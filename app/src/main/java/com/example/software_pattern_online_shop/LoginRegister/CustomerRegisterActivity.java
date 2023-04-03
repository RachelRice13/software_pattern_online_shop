package com.example.software_pattern_online_shop.LoginRegister;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.software_pattern_online_shop.HomePage.CustomerHomeActivity;
import com.example.software_pattern_online_shop.Model.Customer;
import com.example.software_pattern_online_shop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomerRegisterActivity extends AppCompatActivity {
    private static final String TAG = "CustomerRegActivity";
    private TextInputLayout addressLine1LO, townOrCityLO, eircodeLO;
    private Spinner countySpinner;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addressLine1LO = findViewById(R.id.reg_address_line_1);
        townOrCityLO = findViewById(R.id.reg_town_or_city);
        eircodeLO = findViewById(R.id.reg_eircode);
        countySpinner = findViewById(R.id.reg_county_spinner);
        setCountySpinner(countySpinner);

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
        EditText addressL1ET, addressL2ET, addressL3ET, townOrCityET, eircodeET;
        addressL1ET = findViewById(R.id.reg_address_line_1_et);
        addressL2ET = findViewById(R.id.reg_address_line_2_et);
        addressL3ET = findViewById(R.id.reg_address_line_3_et);
        townOrCityET = findViewById(R.id.reg_town_or_city_et);
        eircodeET = findViewById(R.id.reg_eircode_et);

        String addressLine1 = addressL1ET.getText().toString();
        String addressLine2 = addressL2ET.getText().toString();
        String addressLine3 = addressL3ET.getText().toString();
        String townOrCity = townOrCityET.getText().toString();
        String county = countySpinner.getSelectedItem().toString();
        String eircode = eircodeET.getText().toString();

        boolean validAddressLine1 = RegisterActivity.validateBlank(addressLine1, addressLine1LO);
        boolean validTownOrCity = RegisterActivity.validateBlank(townOrCity, townOrCityLO);
        boolean validCounty = validateCounty(county);
        boolean validEircode = validateEircode(eircode);

        if (validAddressLine1 && validTownOrCity && validCounty && validEircode) {
            addCustomerToDB(addressLine1, addressLine2, addressLine3, townOrCity, county, eircode);
        }
    }

    private void setCountySpinner(Spinner countySpinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.county_options));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        countySpinner.setAdapter(spinnerArrayAdapter);
    }

    private boolean validateCounty(String county) {
        if (county.equals("County")) {
            Toast.makeText(this, "'County' is not a valid option", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateEircode(String eircode) {
        if (eircode.length() != 7) {
            eircodeLO.setError("Eircode must contain 7 characters.");
            return false;
        } else {
            eircodeLO.setError(null);
            return true;
        }
    }

    private void addCustomerToDB(String addressLine1, String addressLine2, String addressLine3, String townOrCity, String county, String eircode) {
        String firstName = getIntent().getStringExtra("FIRST_NAME");
        String surname = getIntent().getStringExtra("SURNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String password = getIntent().getStringExtra("PASSWORD");
        String address = addressLine1 + ", " + addressLine2 + ", " + addressLine3 + ", " + townOrCity + ", " + county + ", " + eircode;
        String paymentMethod = "Payment Method";

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Customer customer = new Customer(firstName, surname, email, password, address, paymentMethod);
                            String customerId = FirebaseAuth.getInstance().getUid();;
                            addCustomerToFireStore(customer, customerId);
                        } else {
                            Log.e(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    private void addCustomerToFireStore(Customer customer, String customerId) {
        firebaseFirestore.collection("customer").document(customerId)
                .set(customer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "createUserWithEmail: successful");
                        Intent customerRegisterIntent = new Intent(CustomerRegisterActivity.this, CustomerHomeActivity.class);
                        startActivity(customerRegisterIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CustomerRegisterActivity.this, "Failed to Register Customer", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, e.toString());
                    }
                });
    }
}