package com.group6.noteapp.controller;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.group6.noteapp.R;

/**
 * Register Successful Fragment
 */
public class RegisterFragment03 extends Fragment {
    private String regEmail;        // User register email

    /**
     * Constructor
     */
    public RegisterFragment03() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get user email passed from register step 2 of 2
        if (getArguments() != null) {
            regEmail = getArguments().getString("regEmail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_register03, container, false);

        /* Get TextView and set user email to message */
        TextView txtRegisterSuccess = inflatedView.findViewById(R.id.txtRegisterSuccess);
        txtRegisterSuccess.setText(Html.fromHtml(getString(R.string.email_sent_reg, regEmail)));

        /* Get Button and set On Click Listener */
        MaterialButton btnLogin = inflatedView.findViewById(R.id.btnRegLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Login Fragment
                NavHostFragment.findNavController(RegisterFragment03.this)
                        .navigate(R.id.action_registerFragment03_to_loginFragment);
            }
        });

        return inflatedView;
    }
}