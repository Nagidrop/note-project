/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.group6.noteapp.R;
import com.group6.noteapp.util.ValidationUtils;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

/**
 * Forgot password input email fragment
 */
public class ForgotPasswordFragment01 extends Fragment {

    NoteAppProgressDialog progressDialog;   // Note App progress dialog
    private long lastClickTime;             // User's last click time (to prevent multiple clicks)

    /**
     * Constructor
     */
    public ForgotPasswordFragment01() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its view
     * @param inflater              Layout Inflater
     * @param container             ViewGroup container
     * @param savedInstanceState    saved instance state
     * @return the created View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_forgot_password01, container, false);

        /* Get Button and set On Click Listener */
        MaterialButton btnSubmit = inflatedView.findViewById(R.id.btnForgotSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(getActivity(),
                            "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                /* Get TextInputLayout */
                TextInputLayout inputResetPassEmail = inflatedView.findViewById(R.id.textInputForgotEmail);
                String resetPassEmail = inputResetPassEmail.getEditText().getText().toString();

                // Clear input errors before validation
                clearInputErrors(inputResetPassEmail);

                /* Validate input fields and set errors according to validation results */
                boolean isInputValid = true;
                int emailValidateResult = ValidationUtils.validateEmail(resetPassEmail);

                if (emailValidateResult == 1) {
                    isInputValid = false;
                    inputResetPassEmail.setError("Email must not be empty.");
                } else if (emailValidateResult == 2) {
                    isInputValid = false;
                    inputResetPassEmail.setError("Please use a valid email. (Ex: abc@g.cn)");
                }


                // If all input fields are valid
                if (isInputValid) {
                    // Show progress dialog
                    progressDialog = new NoteAppProgressDialog(getActivity());
                    progressDialog.setUpDialog("Just a moment...",
                            "Please wait while we attempt to send you an email with instructions.");
                    progressDialog.show();

                    // Clear input errors (before navigating)
                    clearInputErrors(inputResetPassEmail);

                    // Send email with reset password instructions
                    sendResetPasswordEmail(resetPassEmail);
                }
            }
        });

        // Inflate the layout for this fragment
        return inflatedView;
    }

    /**
     * Send email with reset password instructions
     *
     * @param emailAddress user's email address
     */
    private void sendResetPasswordEmail(String emailAddress) {
        /* Firebase instance */
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Send password reset email
        auth.sendPasswordResetEmail(emailAddress)
                // If send email successful
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();

                        // Add email to bundle
                        Bundle bundle = new Bundle();
                        bundle.putString("email", emailAddress);

                        // Navigate to Send Email successful page
                        NavHostFragment.findNavController(ForgotPasswordFragment01.this)
                                .navigate(R.id.action_forgotPasswordFragment01_to_forgotPasswordFragment02, bundle);
                    }
                })
                // If send email failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("error", e.getMessage());

                        progressDialog.dismiss();

                        // Show dialog dependent on error
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());

                        if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_USER_NOT_FOUND")) {
                            dialog.setupOKDialog("Email Not Sent",
                                    "There is no account associated with this email address.");
                        } else {
                            dialog.setupOKDialog("Email Not Sent",
                                    "An error occurred while we try to send you instructions. Please try again!");
                        }

                        dialog.create().show();
                    }
                });
    }

    /**
     * Clear input fields' errors
     *
     * @param inputResetPassEmail input reset password email
     */
    private void clearInputErrors(TextInputLayout inputResetPassEmail) {
        /* Set errors to disabled and then enable them again for quick clears */
        inputResetPassEmail.setErrorEnabled(false);

        inputResetPassEmail.setErrorEnabled(true);
    }
}