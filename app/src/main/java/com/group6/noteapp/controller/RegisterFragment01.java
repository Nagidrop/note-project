package com.group6.noteapp.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.noteapp.R;
import com.group6.noteapp.util.ValidationUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment01 extends Fragment {

    View inflatedView;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment01() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment01 newInstance(String param1, String param2) {
        RegisterFragment01 fragment = new RegisterFragment01();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_register01, container, false);
        mAuth = FirebaseAuth.getInstance();

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

                inputRegEmail.setErrorEnabled(false);
                inputRegPassword.setErrorEnabled(false);
                inputRegConfirmPassword.setErrorEnabled(false);

                inputRegEmail.setErrorEnabled(true);
                inputRegPassword.setErrorEnabled(true);
                inputRegConfirmPassword.setErrorEnabled(true);

                boolean isInputValid = true;
                int validateEmailResult = ValidationUtils.validateEmail(regEmail);
                int validatePasswordResult = ValidationUtils.validatePasswordReg(regPassword, regConfirmPassword);

                if (validateEmailResult == 1) {
                    isInputValid = false;
                    inputRegEmail.setError("Email must not be empty!");
                } else if (validateEmailResult == 2) {
                    isInputValid = false;
                    inputRegEmail.setError("Please use a valid email! (Ex: abc@g.cn)");
                }

                if (validatePasswordResult == 1) {
                    isInputValid = false;
                    inputRegPassword.setError("Password must not be empty!");
                } else if (validatePasswordResult == 2){
                    isInputValid = false;
                    inputRegPassword.setError("Password must be at least 8 characters!");
                } else if (validatePasswordResult == 3){
                    isInputValid = false;
                    inputRegConfirmPassword.setError("Password confirmation must match password!");
                }

                if (isInputValid){
                    RegisterFragment02 registerFragment02 = new RegisterFragment02();

                    Bundle regData = new Bundle();
                    regData.putString("regEmail", regEmail);
                    regData.putString("regPassword", regPassword);

                    registerFragment02.setArguments(regData);

                    inputRegEmail.setErrorEnabled(false);
                    inputRegPassword.setErrorEnabled(false);
                    inputRegConfirmPassword.setErrorEnabled(false);

                    inputRegEmail.setErrorEnabled(true);
                    inputRegPassword.setErrorEnabled(true);
                    inputRegConfirmPassword.setErrorEnabled(true);

                    NavHostFragment.findNavController(RegisterFragment01.this).navigate(R.id.action_registerFragment01_to_registerFragment02, regData);
                }
            }
        });

        return inflatedView;
    }
}