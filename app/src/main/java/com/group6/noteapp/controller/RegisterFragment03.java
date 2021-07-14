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

public class RegisterFragment03 extends Fragment {
    private String regEmail;

    public RegisterFragment03() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            regEmail = getArguments().getString("regEmail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_register03, container, false);

        TextView txtRegisterSuccess = inflatedView.findViewById(R.id.txtRegisterSuccess);
        txtRegisterSuccess.setText(Html.fromHtml(getString(R.string.email_sent_reg, regEmail)));

        // Get register button
        MaterialButton btnRegister = inflatedView.findViewById(R.id.btnRegLogin);
        // Set navigate to register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(RegisterFragment03.this)
                        .navigate(R.id.action_registerFragment03_to_loginFragment);
            }
        });

        // Inflate the layout for this fragment
        return inflatedView;
    }
}