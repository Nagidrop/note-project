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
 * Forgot password send email successful screen
 */
public class ForgotPasswordFragment02 extends Fragment {
    private String email;       // user's email

    /**
     * Constructor
     */
    public ForgotPasswordFragment02() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get email passed from forgot password screen */
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_forgot_password02, container, false);

        /* Get TextView and set user email to message */
        TextView txtForgotPassword = inflatedView.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setText(Html.fromHtml(getString(R.string.email_sent_forgot_pass, email)));

        /* Get Button and set On Click Listener */
        MaterialButton btnForgotLogin = inflatedView.findViewById(R.id.btnForgotLogin);
        btnForgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                NavHostFragment.findNavController(ForgotPasswordFragment02.this)
                        .navigate(R.id.action_forgotPasswordFragment02_to_loginFragment);
            }
        });

        return inflatedView;
    }
}