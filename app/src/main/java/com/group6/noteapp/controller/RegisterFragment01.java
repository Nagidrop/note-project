package com.group6.noteapp.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.group6.noteapp.R;

import static android.content.ContentValues.TAG;
import static com.google.firebase.FirebaseError.ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL;

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
        TextInputLayout etEmail = inflatedView.findViewById(R.id.textInputRegEmail);
        TextInputLayout etPassword = inflatedView.findViewById(R.id.textInputRegPassword);
        TextInputLayout etConfirmPassword = inflatedView.findViewById(R.id.textInputRegRePassword);
        progressDialog = new ProgressDialog(getActivity());
        MaterialButton btnNext = inflatedView.findViewById(R.id.btnNext01);

//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//
//                /* Create local variables to store the EditText Views' current values */
//
//                String regEmail = etEmail.getEditText().getText().toString().trim();
//                String regPassword = etPassword.getEditText().getText().toString().trim();
//                String regConfirmPassword = etConfirmPassword.getEditText().getText().toString().trim();
//
//                /* show progress dialog*/
//                progressDialog.setTitle("Login");
//                progressDialog.setMessage("Please wait while check your credentials");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.show();
//
//                mAuth.createUserWithEmailAndPassword(regEmail, regPassword)
//                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    progressDialog.dismiss();
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    NavHostFragment.findNavController(RegisterFragment01.this).navigate(R.id.action_registerFragment01_to_registerFragment02);
//                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException.class){
//                                    FirebaseAuthUserCollisionException exception =
//                                            (FirebaseAuthUserCollisionException) task.getException();
//                                    if (Integer.parseInt(exception.getErrorCode()) ==
//                                            ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL) {
//
//                                    }
//
//                                }
//                            }
//                        });
//            }
//        });

        return inflatedView;
    }
}