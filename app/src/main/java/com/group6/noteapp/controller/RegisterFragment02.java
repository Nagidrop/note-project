package com.group6.noteapp.controller;

import android.content.ContentResolver;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group6.noteapp.R;
import com.group6.noteapp.model.Note;
import com.group6.noteapp.model.Notebook;
import com.group6.noteapp.model.User;
import com.group6.noteapp.util.Constants;
import com.group6.noteapp.util.ValidationUtils;
import com.group6.noteapp.view.NoteAppDialog;
import com.group6.noteapp.view.NoteAppProgressDialog;

import org.jetbrains.annotations.NotNull;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment02#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment02 extends Fragment {

    private View inflatedView;
    private NoteAppProgressDialog progressDialog;

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

                clearInputErrors(inputRegFullName, inputRegAddress);

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
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAu = FirebaseAuth.getInstance();

                    clearInputErrors(inputRegFullName, inputRegAddress);

                    /* show progress dialog*/
                    progressDialog = new NoteAppProgressDialog(getActivity());
                    progressDialog.setUpDialog("Just a moment...",
                            "Please wait while we set up your account for Note App.");
                    progressDialog.show();

                    mAu.createUserWithEmailAndPassword(regEmail, regPassword)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser firebaseUser = authResult.getUser();

                                    User newUser = new User();
                                    newUser.setFullName(regFullname);
                                    newUser.setBirthdate(regBirthdate);
                                    newUser.setAddress(regAddress);

                                    setUpUserInfo(newUser, firebaseUser, db);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressDialog.dismiss();

                                    Log.e(Constants.REGISTER_ERROR, "Error creating account", e);

                                    NoteAppDialog dialog = new NoteAppDialog(getActivity());

                                    switch (((FirebaseAuthException) e).getErrorCode()) {
                                        case "ERROR_EMAIL_ALREADY_IN_USE":
                                            dialog.setupOKDialog("Registration Failed",
                                                    "Email address is already in use by another account. Please use a different one.");

                                            break;

                                        default:
                                            dialog.setupOKDialog("Registration Failed",
                                                    "An error occurred during your account setup. Please try register again!");

                                            break;
                                    }
                                    
                                    dialog.create().show();
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

    private void clearInputErrors(TextInputLayout inputRegFullName, TextInputLayout inputRegAddress) {
        inputRegFullName.setErrorEnabled(false);
        inputRegAddress.setErrorEnabled(false);

        inputRegFullName.setErrorEnabled(true);
        inputRegAddress.setErrorEnabled(true);
    }

    private void addDefaultNotebook(DocumentReference userInfoDoc, FirebaseUser firebaseUser) {
        Notebook defaultNotebook = new Notebook();
        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

        CollectionReference userNotebookCol = userInfoDoc.collection("notebooks");

        userNotebookCol.add(defaultNotebook)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addWelcomeNote(documentReference, firebaseUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();

                        Log.e(Constants.REGISTER_ERROR, "Error adding default notebook", e);

                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Registration Failed",
                                "An error occurred during your account setup. Please try register again!");
                        dialog.create().show();
                    }
                });
    }

    private void addWelcomeNote(DocumentReference userDefNotebookDoc, FirebaseUser firebaseUser) {
        Note welcomeNote = new Note();
        welcomeNote.setTitle(Constants.WELCOME_NOTE_TITLE);
        welcomeNote.setContent(Constants.WELCOME_NOTE_CONTENT);

        Note welcomeNote2 = new Note();
        welcomeNote2.setTitle("Test note - delete at release");
        welcomeNote2.setContent("I don't know what you did, Fry, but once again, you screwed up! Now all the planets are gonna start cracking wise about our mamas. When will that be? Uh, is the puppy mechanical in any way? She also liked to shut up!\n" +
                "\n" +
                "Who am I making this out to? Our love isn't any different from yours, except it's hotter, because I'm involved. Okay, it's 500 dollars, you have no choice of carrier, the battery can't hold the charge and the reception isn't very…");

        Note welcomeNote3 = new Note();
        welcomeNote3.setTitle("Test note but intentionally exceeds longer than two lines title - delete at release");
        welcomeNote3.setContent("When I was first asked to make a film about my nephew, Hubert Farnsworth, I thought \"Why should I?\" Then later, Leela made the film. But if I did make it, you can bet there would have been more topless women on motorcycles. Roll film! You are the last hope of the universe.");

        CollectionReference userNoteCollection = userDefNotebookDoc.collection("notes");
        userNoteCollection.add(welcomeNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();

                        firebaseUser.sendEmailVerification();

                        Bundle regData = new Bundle();
                        regData.putString("regEmail", regEmail);

                        NavHostFragment.findNavController(RegisterFragment02.this)
                                .navigate(R.id.action_registerFragment02_to_registerFragment03, regData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();

                        Log.e(Constants.REGISTER_ERROR, "Error adding welcome note", e);

                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Registration Failed",
                                "An error occurred during your account setup. Please try register again!");
                        dialog.create().show();
                    }
                });

        userNoteCollection.add(welcomeNote2);
        userNoteCollection.add(welcomeNote3);
    }

    private void setUpUserInfo(User newUser, FirebaseUser firebaseUser, FirebaseFirestore db) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri profilePic = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getResources().getResourcePackageName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceTypeName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceEntryName(R.drawable.img_profile_pic));

        final StorageReference profilePictureRef = storageRef.child("images/" + firebaseUser.getUid() + "/profilePicture.png");

        profilePictureRef.putFile(profilePic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UserProfileChangeRequest profileSetup = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getFullName())
                                .build();

                        firebaseUser.updateProfile(profileSetup)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DocumentReference userInfoDoc = db.collection("users").document(firebaseUser.getUid());

                                        userInfoDoc.set(newUser)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot written with ID: " + firebaseUser.getUid());

                                                        addDefaultNotebook(userInfoDoc, firebaseUser);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Log.e(Constants.REGISTER_ERROR, "Error updating user info", e);

                                                        progressDialog.dismiss();
                                                        // handle error
                                                        String error = e.getMessage();

                                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                                        dialog.setupOKDialog("Registration Failed",
                                                                "An unknown error occurred!\nError message:\n\"" + error + "\"");
                                                        dialog.create().show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.e(Constants.REGISTER_ERROR, "Error updating display name", e);

                                        progressDialog.dismiss();

                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                        dialog.setupOKDialog("Registration Failed",
                                                "An error occurred during your account setup. Please try register again!");
                                        dialog.create().show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e(Constants.REGISTER_ERROR, "Error uploading profile picture", e);

                progressDialog.dismiss();

                NoteAppDialog dialog = new NoteAppDialog(getActivity());
                dialog.setupOKDialog("Registration Failed",
                        "An error occurred during your account setup. Please try register again!");
                dialog.create().show();
            }
        });
    }
}
