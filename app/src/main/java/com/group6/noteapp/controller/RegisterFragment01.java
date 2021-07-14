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

public class RegisterFragment01 extends Fragment {


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

        /* Get EditText Views */
        TextInputLayout inputRegEmail = inflatedView.findViewById(R.id.textInputRegEmail);
        TextInputLayout inputRegPassword = inflatedView.findViewById(R.id.textInputRegPassword);
        TextInputLayout inputRegConfirmPassword = inflatedView.findViewById(R.id.textInputRegRePassword);

        MaterialButton btnNext = inflatedView.findViewById(R.id.btnNext01);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create local variables to store the EditText Views' current values */

                String regEmail = inputRegEmail.getEditText().getText().toString().trim();
                String regPassword = inputRegPassword.getEditText().getText().toString().trim();
                String regConfirmPassword = inputRegConfirmPassword.getEditText().getText().toString().trim();

                clearInputErrors(inputRegEmail, inputRegPassword, inputRegConfirmPassword);

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

                if (isInputValid) {
                    clearInputErrors(inputRegEmail, inputRegPassword, inputRegConfirmPassword);

                    RegisterFragment02 registerFragment02 = new RegisterFragment02();

                    Bundle regData = new Bundle();
                    regData.putString("regEmail", regEmail);
                    regData.putString("regPassword", regPassword);

                    registerFragment02.setArguments(regData);

                    NavHostFragment.findNavController(RegisterFragment01.this)
                            .navigate(R.id.action_registerFragment01_to_registerFragment02, regData);
                }
            }
        });

        return inflatedView;
    }

    private void clearInputErrors(TextInputLayout inputRegEmail, TextInputLayout inputRegPassword,
                                  TextInputLayout inputRegConfirmPassword) {
        inputRegEmail.setErrorEnabled(false);
        inputRegPassword.setErrorEnabled(false);
        inputRegConfirmPassword.setErrorEnabled(false);

        inputRegEmail.setErrorEnabled(true);
        inputRegPassword.setErrorEnabled(true);
        inputRegConfirmPassword.setErrorEnabled(true);
    }
}