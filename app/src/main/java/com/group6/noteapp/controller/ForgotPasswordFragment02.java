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


public class ForgotPasswordFragment02 extends Fragment {
    private String email;

    public ForgotPasswordFragment02() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View inflatedView = inflater.inflate(R.layout.fragment_forgot_password02, container, false);

        TextView txtForgotPassword = inflatedView.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setText(Html.fromHtml(getString(R.string.email_sent_forgot_pass, email)));

        MaterialButton btnForgotLogin = inflatedView.findViewById(R.id.btnForgotLogin);
        btnForgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                NavHostFragment.findNavController(ForgotPasswordFragment02.this)
                        .navigate(R.id.action_forgotPasswordFragment02_to_loginFragment);
            }
        });

        // Inflate the layout for this fragment
        return inflatedView;
    }
}