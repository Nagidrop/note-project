package com.group6.noteapp.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.noteapp.R;
import com.group6.noteapp.model.User;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment02#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment02 extends Fragment {

    View inflatedView;
    private FirebaseAuth mAu;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String regEmail;
    private String regPassword;

    public RegisterFragment02() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment02.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment02 newInstance(String param1, String param2) {
        RegisterFragment02 fragment = new RegisterFragment02();
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
            regEmail = getArguments().getString("regEmail");
            regPassword = getArguments().getString("regPassword");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        inflatedView = inflater.inflate(R.layout.fragment_register02, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAu = FirebaseAuth.getInstance();
        /* Get EditText Views */

        TextInputLayout inputRegFullName = inflatedView.findViewById(R.id.textInputRegFullname);
        TextInputLayout inputRegBirthdate = inflatedView.findViewById(R.id.textInputRegBirthdate);
        TextInputLayout inputRegAddress = inflatedView.findViewById(R.id.textInputRegAddress);

        MaterialButton btnLogin = inflatedView.findViewById(R.id.btnLoginReg);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                /* Create local variables to store the EditText Views' current values */

                String regFullname = inputRegFullName.getEditText().getText().toString();
                String regBirthdate = inputRegBirthdate.getEditText().getText().toString();
                String regAddress = inputRegAddress.getEditText().getText().toString();
                // Create a new user with a first and last name

                User newUser = new User();
                newUser.setFullName(regFullname);
                newUser.setBirthdate(regBirthdate);
                newUser.setAddress(regAddress);

                ProgressDialog progressDialog = new ProgressDialog(getActivity());

                /* show progress dialog*/
                progressDialog.setTitle("Login");
                progressDialog.setMessage("Please wait while check your credentials");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAu.createUserWithEmailAndPassword(regEmail, regPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    String userUid = task.getResult().getUser().getUid();

//                                    db.collection("users").document(userUid).set(newUser);
                                    db.collection("users")
                                            .document(userUid)
                                            .set(newUser)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot written with ID: " + userUid);
                                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });
                                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    progressDialog.dismiss();
                                    FirebaseAuthUserCollisionException exception =
                                            (FirebaseAuthUserCollisionException) task.getException();
                                    if (exception.getErrorCode().equalsIgnoreCase("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {

                                    }
                                }
                            }
                        });
            }
        });


        return inflatedView;
    }
}