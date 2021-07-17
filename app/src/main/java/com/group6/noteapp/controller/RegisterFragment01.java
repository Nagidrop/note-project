/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.group6.noteapp.R;
import com.group6.noteapp.util.ValidationUtils;

/**
 * Register Fragment step 1 of 2
 */
public class RegisterFragment01 extends Fragment {

    /**
     * Constructor
     */
    public RegisterFragment01() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_register01, container, false);

        /* Get TextInputLayout Views */
        TextInputLayout inputRegEmail = inflatedView.findViewById(R.id.textInputRegEmail);
        TextInputLayout inputRegPassword = inflatedView.findViewById(R.id.textInputRegPassword);
        TextInputLayout inputRegConfirmPassword = inflatedView.findViewById(R.id.textInputRegRePassword);

        /* Get Button and set On Click Listener */
        MaterialButton btnNext = inflatedView.findViewById(R.id.btnNext01);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create local variables to store the Inputs' current values */
                String regEmail = inputRegEmail.getEditText().getText().toString().trim();
                String regPassword = inputRegPassword.getEditText().getText().toString().trim();
                String regConfirmPassword = inputRegConfirmPassword.getEditText().getText().toString().trim();

                // Clear input errors before validation
                clearInputErrors(inputRegEmail, inputRegPassword, inputRegConfirmPassword);

                /* Validate input fields and set errors according to validation results */
                boolean isInputValid = true;
                int validateEmailResult = ValidationUtils.validateEmail(regEmail);
                int validatePasswordResult = ValidationUtils.validatePasswordReg(regPassword, regConfirmPassword);

                if (validateEmailResult == 1) {
                    isInputValid = false;
                    inputRegEmail.setError("Email must not be empty.");
                } else if (validateEmailResult == 2) {
                    isInputValid = false;
                    inputRegEmail.setError("Please use a valid email. (Ex: abc@g.cn)");
                }

                if (validatePasswordResult == 1) {
                    isInputValid = false;
                    inputRegPassword.setError("Password must not be empty.");
                } else if (validatePasswordResult == 2) {
                    isInputValid = false;
                    inputRegPassword.setError("Password must be at least 8 characters.");
                } else if (validatePasswordResult == 3) {
                    isInputValid = false;
                    inputRegConfirmPassword.setError("Password confirmation doesn't match.");
                }

                // If all input fields are valid
                if (isInputValid) {
                    // Clear input errors again (before navigating to the next fragment)
                    clearInputErrors(inputRegEmail, inputRegPassword, inputRegConfirmPassword);

                    /* Create Register Fragment step 2 of 2 and pass register data to it */
                    RegisterFragment02 registerFragment02 = new RegisterFragment02();

                    Bundle regData = new Bundle();
                    regData.putString("regEmail", regEmail);
                    regData.putString("regPassword", regPassword);

                    registerFragment02.setArguments(regData);

                    // Navigate to Register Fragment step 2 of 2
                    NavHostFragment.findNavController(RegisterFragment01.this)
                            .navigate(R.id.action_registerFragment01_to_registerFragment02, regData);
                }
            }
        });

        return inflatedView;
    }

    /**
     * Clear input fields' errors
     *
     * @param inputRegEmail           email input layout
     * @param inputRegPassword        password input layout
     * @param inputRegConfirmPassword confirm password input layout
     */
    private void clearInputErrors(TextInputLayout inputRegEmail, TextInputLayout inputRegPassword,
                                  TextInputLayout inputRegConfirmPassword) {
        /* Set errors to disabled and then enable them again for quick clears */
        inputRegEmail.setErrorEnabled(false);
        inputRegPassword.setErrorEnabled(false);
        inputRegConfirmPassword.setErrorEnabled(false);

        inputRegEmail.setErrorEnabled(true);
        inputRegPassword.setErrorEnabled(true);
        inputRegConfirmPassword.setErrorEnabled(true);
    }
}