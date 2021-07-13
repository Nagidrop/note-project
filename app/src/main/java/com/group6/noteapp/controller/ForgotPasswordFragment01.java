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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment01 extends Fragment {

    View inflatedView;
    TextInputLayout inputResetPassEmail;
    NoteAppProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment01() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment01.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment01 newInstance(String param1, String param2) {
        ForgotPasswordFragment01 fragment = new ForgotPasswordFragment01();
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
        inflatedView = inflater.inflate(R.layout.fragment_forgot_password01, container, false);
        //get text Input text layout
        MaterialButton btnSubmit = inflatedView.findViewById(R.id.btnForgotSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputResetPassEmail = inflatedView.findViewById(R.id.textInputForgotEmail);
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