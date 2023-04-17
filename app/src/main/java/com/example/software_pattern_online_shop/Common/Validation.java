package com.example.software_pattern_online_shop.Common;

import android.content.Context;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean validateBlank(String text, TextInputLayout layout) {
        if (text.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validatePassword(String password, TextInputLayout passwordLO) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        Matcher matcher = pattern.matcher(password);
        if (password.isEmpty()) {
            passwordLO.setError("This is Required");
            return false;
        } else {
            if (!matcher.matches()) {
                passwordLO.setError("Password must be at least 8 characters and contain both uppercase and lowercase characters/numbers/special characters");
                return false;
            } else {
                passwordLO.setError(null);
                return true;
            }
        }
    }

    public static boolean validateCounty(String county, Context context) {
        if (county.equals("County")) {
            Toast.makeText(context, "'County' is not a valid option", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateEircode(String eircode, TextInputLayout eircodeLO) {
        if (eircode.length() != 7) {
            eircodeLO.setError("Eircode must contain 7 characters.");
            return false;
        } else {
            eircodeLO.setError(null);
            return true;
        }
    }

    public static boolean validateEmployeeNumber(String employeeNum, TextInputLayout employeeNumLO) {
        if (employeeNum.length() != 8) {
            employeeNumLO.setError("Employee Number must contain 8 digits.");
            return false;
        } else {
            employeeNumLO.setError(null);
            return true;
        }
    }

    public static boolean validateImage(String imagePath, Context context) {
        if (imagePath.equals(" ")) {
            Toast.makeText(context, "Choose an image", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static boolean validateNumber(String number, TextInputLayout layout) {
        if (number.isEmpty()) {
            layout.setError("This is Required");
            return false;
        } else {
            String firstDigit = number.substring(0,1);
            if (firstDigit.equals("0")) {
                layout.setError("Number must be greater than 0");
                return false;
            } else {
                layout.setError(null);
                return true;
            }
        }
    }
}
