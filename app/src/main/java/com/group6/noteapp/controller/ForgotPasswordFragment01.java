package com.group6.noteapp.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class ForgotPasswordFragment01 extends Fragment {

    NoteAppProgressDialog progressDialog;


    public ForgotPasswordFragment01() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_forgot_password01, container, false);
        //get text Input text layout
        MaterialButton btnSubmit = inflatedView.findViewById(R.id.btnForgotSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout inputResetPassEmail = inflatedView.findViewById(R.id.textInputForgotEmail);
                String resetPassEmail = inputResetPassEmail.getEditText().getText().toString();

                clearInputErrors(inputResetPassEmail);

                boolean isInputValid = true;
                int emailValidateResult = ValidationUtils.validateEmail(resetPassEmail);

                if (emailValidateResult == 1) {
                    isInputValid = false;
                    inputResetPassEmail.setError("Email must not be empty.");
                } else if (emailValidateResult == 2) {
                    isInputValid = false;
                    inputResetPassEmail.setError("Please use a valid email. (Ex: abc@g.cn)");
                }

                if (isInputValid){
                    progressDialog = new NoteAppProgressDialog(getActivity());
                    progressDialog.setUpDialog("Just a moment...",
                            "Please wait while we attempt to send you an email with instructions.");
                    progressDialog.show();

                    clearInputErrors(inputResetPassEmail);

                    sendEmailResetPassword(resetPassEmail);
                }
            }
        });

        // Inflate the layout for this fragment
        return inflatedView;
    }

    private void sendEmailResetPassword(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString("email", emailAddress);
                        NavHostFragment.findNavController(ForgotPasswordFragment01.this)
                                .navigate(R.id.action_forgotPasswordFragment01_to_forgotPasswordFragment02, bundle);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("error", e.getMessage());

                        progressDialog.dismiss();

                        NoteAppDialog dialog = new NoteAppDialog(getActivity());

                        switch (((FirebaseAuthException) e).getErrorCode()){
                            case "ERROR_USER_NOT_FOUND":
                                dialog.setupOKDialog("Email Not Sent",
                                        "There is no account associated with this email address.");

                                break;

                            default:
                                dialog.setupOKDialog("Email Not Sent",
                                        "An error occurred while we try to send you instructions. Please try again!");

                                break;
                        }

                        dialog.create().show();
                    }
                });
    }

    private void clearInputErrors(TextInputLayout inputResetPassEmail){
        inputResetPassEmail.setErrorEnabled(false);

        inputResetPassEmail.setErrorEnabled(true);
    }
}