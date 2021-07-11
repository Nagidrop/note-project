package com.group6.noteapp.controller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.noteapp.Constants;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.model.User;
import com.group6.noteapp.util.ValidationUtils;

import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment02#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment02 extends Fragment {

    private View inflatedView;
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

        TextInputLayout inputRegFullName = inflatedView.findViewById(R.id.textInputRegFullName);
        TextInputLayout inputRegBirthdate = inflatedView.findViewById(R.id.textInputRegBirthdate);
        TextInputEditText inputRegBirthdateEditText = inflatedView.findViewById(R.id.textInputRegBirthdateEditText);
        TextInputLayout inputRegAddress = inflatedView.findViewById(R.id.textInputRegAddress);

        inputRegBirthdateEditText.setInputType(InputType.TYPE_NULL);
        inputRegBirthdateEditText.setKeyListener(null);
        inputRegBirthdateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(inputRegBirthdateEditText);
            }
        });

        inputRegBirthdateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputRegBirthdateEditText.performClick();
                }
            }
        });

        MaterialButton btnLogin = inflatedView.findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create local variables to store the EditText Views' current values */
                String regFullname = inputRegFullName.getEditText().getText().toString();
                String regBirthdate = inputRegBirthdate.getEditText().getText().toString();
                String regAddress = inputRegAddress.getEditText().getText().toString();
                // Create a new user with a first and last name
                inputRegFullName.setErrorEnabled(false);
                inputRegAddress.setErrorEnabled(false);

                inputRegFullName.setErrorEnabled(true);
                inputRegAddress.setErrorEnabled(true);

                boolean isInputValid = true;
                int validateFullNameResult = ValidationUtils.validateFullName(regFullname);
                int validateAddressResult = ValidationUtils.validateAddress(regAddress);

                if (validateFullNameResult == 1) {
                    isInputValid = false;
                    inputRegFullName.setError("Full Name must not be empty.");
                } else if (validateFullNameResult == 2) {
                    isInputValid = false;
                    inputRegFullName.setError("Full Name must have at least 2 words.");
                }

                if (validateAddressResult == 1) {
                    isInputValid = false;
                    inputRegAddress.setError("Please use a valid address. (Ex: 3/2 Str.)");
                }

                if (isInputValid) {
                    ProgressDialog progressDialog = new ProgressDialog(getActivity());

                    /* show progress dialog*/
                    progressDialog.setTitle("Registering...");
                    progressDialog.setMessage("Please wait while we register you in Note App.");
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
                                        FirebaseUser firebaseUser = task.getResult().getUser();
                                        String userUid = firebaseUser.getUid();

                                        User newUser = new User();
                                        newUser.setFullName(regFullname);
                                        newUser.setBirthdate(regBirthdate);
                                        newUser.setAddress(regAddress);

                                        DocumentReference userInfoDoc = db.collection("users").document(userUid);

                                        userInfoDoc.set(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot written with ID: " + userUid);

                                                        Notebook defaultNotebook = new Notebook();
                                                        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

                                                        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks")
                                                                .document(defaultNotebook.getTitle());
                                                        userDefNotebookDoc.set(defaultNotebook);

                                                        Note welcomeNote = new Note();
                                                        welcomeNote.setTitle(Constants.WELCOME_NOTE_TITLE);
                                                        welcomeNote.setContent(Constants.WELCOME_NOTE_CONTENT);

                                                        Note welcomeNote2 = new Note();
                                                        welcomeNote2.setTitle("Test note - delete at release");
                                                        welcomeNote2.setContent(UUID.randomUUID().toString() + UUID.randomUUID().toString()
                                                                + UUID.randomUUID().toString() + UUID.randomUUID().toString());

                                                        Note welcomeNote3 = new Note();
                                                        welcomeNote3.setTitle("Test note - delete at release");
                                                        welcomeNote3.setContent(UUID.randomUUID().toString() + UUID.randomUUID().toString()
                                                                + UUID.randomUUID().toString() + UUID.randomUUID().toString());

                                                        CollectionReference userDefNoteCollection = userDefNotebookDoc.collection("notes");
                                                        userDefNoteCollection.add(welcomeNote);
                                                        userDefNoteCollection.add(welcomeNote2);
                                                        userDefNoteCollection.add(welcomeNote3);

                                                        firebaseUser.sendEmailVerification();

                                                        Bundle regData = new Bundle();
                                                        regData.putString("regEmail", regEmail);

                                                        NavHostFragment.findNavController(RegisterFragment02.this)
                                                                .navigate(R.id.action_registerFragment02_to_registerFragment03, regData);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error adding document", e);
                                                    }
                                                });
                                    } else {
                                        // handle error
                                        progressDialog.dismiss();
                                        String error = task.getException().getMessage();

                                        if (error.equalsIgnoreCase("The email address is already in use by another account.")) {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                            alert.setTitle("Registration Failed");                                                  // set dialog title
                                            alert.setMessage("Email address is already in use. Please use a different email address!");     // set dialog message
                                            alert.setCancelable(false);

                                            alert.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * To register activity
                                                         * @param dialog dialog
                                                         * @param which which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                            alert.create().show();
                                        } else {
                                            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                            alert.setTitle("Registration Failed");                                                  // set dialog title
                                            alert.setMessage("An unknown error occurred!\nError message:\n\"" + error + "\"");        // set dialog message
                                            alert.setCancelable(false);

                                            alert.setPositiveButton("OK",
                                                    new DialogInterface.OnClickListener() {
                                                        /**
                                                         * To register activity
                                                         * @param dialog dialog
                                                         * @param which which
                                                         */
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });

                                            alert.create().show();
                                        }
                                    }
                                }
                            });
                }
            }
        });

        return inflatedView;
    }

    public void openDatePicker(TextInputEditText inputRegBirthdateEditText) {
        DialogFragment dialogFragment = new DatePickerFragment(inputRegBirthdateEditText);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
}
