/*
 * Group 06 SE1402
 */

package com.group6.noteapp.controller;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
 * Register Fragment step 2 of 2
 */
public class RegisterFragment02 extends Fragment {

    private NoteAppProgressDialog progressDialog;   // Progress dialog
    private String regEmail;                        // Register email    (from register step 1 of 2)
    private String regPassword;                     // Register password (from register step 1 of 2)
    private long lastClickTime;                     // User's last click time (to prevent multiple clicks)

    /**
     * Constructor
     */
    public RegisterFragment02() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get register data passed from register step 1 of 2 */
        if (getArguments() != null) {
            regEmail = getArguments().getString("regEmail");
            regPassword = getArguments().getString("regPassword");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_register02, container, false);

        /* Get TextInputLayout Views */
        TextInputLayout inputRegFullName = inflatedView.findViewById(R.id.textInputRegFullName);
        TextInputLayout inputRegBirthdate = inflatedView.findViewById(R.id.textInputRegBirthdate);
        TextInputEditText inputRegBirthdateEditText = inflatedView.findViewById(R.id.textInputRegBirthdateEditText);
        TextInputLayout inputRegAddress = inflatedView.findViewById(R.id.textInputRegAddress);

        /* Replace Birthday input to open Date Picker Dialog */
        inputRegBirthdateEditText.setInputType(InputType.TYPE_NULL);
        inputRegBirthdateEditText.setKeyListener(null);

        /* Set On Click to open Date Picker and also perform On Click on focus */
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

        /* Get Button and set On Click Listener */
        MaterialButton btnRegister = inflatedView.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Multiple click prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    // Show message to notify user of fast clicks
                    Toast.makeText(getActivity(), "You are tapping too fast. Please wait.", Toast.LENGTH_SHORT).show();

                    return;
                }

                // Update last click time
                lastClickTime = SystemClock.elapsedRealtime();

                /* Create local variables to store the Inputs' current values */
                String regFullname = inputRegFullName.getEditText().getText().toString();
                String regBirthdate = inputRegBirthdate.getEditText().getText().toString();
                String regAddress = inputRegAddress.getEditText().getText().toString();

                // Clear input errors before validation
                clearInputErrors(inputRegFullName, inputRegAddress);

                /* Validate input fields and set errors according to validation results */
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

                // If all input fields are valid
                if (isInputValid) {
                    /* Show progress dialog */
                    progressDialog = new NoteAppProgressDialog(getActivity());
                    progressDialog.setUpDialog("Just a moment...",
                            "Please wait while we set up your account for Note App.");
                    progressDialog.show();

                    /* Firebase instances */
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseAuth mAu = FirebaseAuth.getInstance();

                    // Clear input errors (before navigating)
                    clearInputErrors(inputRegFullName, inputRegAddress);

                    // Create new user with input email and password
                    mAu.createUserWithEmailAndPassword(regEmail, regPassword)
                            // If creation successful
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    /* Get new user and generate new object with user info to setup */
                                    FirebaseUser firebaseUser = authResult.getUser();

                                    User newUser = new User();
                                    newUser.setFullName(regFullname);
                                    newUser.setBirthdate(regBirthdate);
                                    newUser.setAddress(regAddress);

                                    // Setup user info
                                    setupUserInfo(newUser, firebaseUser, db);
                                }
                            })
                            // If failed
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    progressDialog.dismiss();   // Dismiss dialog

                                    Log.e(Constants.REGISTER_ERROR, "Error creating account", e);

                                    NoteAppDialog dialog = new NoteAppDialog(getActivity());    // Instantiate new dialog

                                    // Set dialog title and message based on exception
                                    if (((FirebaseAuthException) e).getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")) {
                                        dialog.setupOKDialog("Registration Failed",
                                                "Email address is already in use by another account. Please use a different one.");
                                    } else {
                                        dialog.setupOKDialog("Registration Failed",
                                                "An error occurred during your account setup. Please try register again!");
                                    }

                                    // Show dialog
                                    dialog.create().show();
                                }
                            });
                }
            }
        });

        return inflatedView;
    }

    /**
     * Open Date Picker Dialog
     *
     * @param inputRegBirthdateEditText birthdate input edittext
     */
    public void openDatePicker(TextInputEditText inputRegBirthdateEditText) {
        DialogFragment dialogFragment = new DatePickerFragment(inputRegBirthdateEditText);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    /**
     * Clear input fields' errors
     *
     * @param inputRegFullName full name input layout
     * @param inputRegAddress  address input layout
     */
    private void clearInputErrors(TextInputLayout inputRegFullName, TextInputLayout inputRegAddress) {
        /* Set errors to disabled and then enable them again for quick clears */
        inputRegFullName.setErrorEnabled(false);
        inputRegAddress.setErrorEnabled(false);

        inputRegFullName.setErrorEnabled(true);
        inputRegAddress.setErrorEnabled(true);
    }

    /**
     * Setup user info for newly created account
     *
     * @param newUser      newly created user
     * @param firebaseUser Firebase user obj
     * @param db           Firestore obj
     */
    private void setupUserInfo(User newUser, FirebaseUser firebaseUser, FirebaseFirestore db) {

        /* Get storage and storage reference */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Profile picture URI points to project's drawable profile image
        Uri profilePic = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getResources().getResourcePackageName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceTypeName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceEntryName(R.drawable.img_profile_pic));

        // Profile picture storage reference
        final StorageReference profilePictureRef = storageRef.child(firebaseUser.getUid() + "/images/profilePicture.png");

        // Upload profile picture
        profilePictureRef.putFile(profilePic)
                // If upload successful
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Set display name for user
                        UserProfileChangeRequest profileSetup = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getFullName())
                                .build();

                        // Update display name
                        firebaseUser.updateProfile(profileSetup)
                                // If update display name successful
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Get user document
                                        DocumentReference userDoc = db.collection("users").document(firebaseUser.getUid());

                                        // Set up other info for user (birthdate, address, ...)
                                        userDoc.set(newUser)
                                                // If set up successfully
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "DocumentSnapshot written with ID: " + firebaseUser.getUid());

                                                        // Add default notebook for user
                                                        addDefaultNotebook(userDoc, firebaseUser);
                                                    }
                                                })
                                                // If set up failed
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull @NotNull Exception e) {
                                                        Log.e(Constants.REGISTER_ERROR, "Error updating user info", e);

                                                        progressDialog.dismiss();

                                                        // Show dialog with error info
                                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                                        dialog.setupOKDialog("Registration Failed",
                                                                "An unknown error occurred!\nError message:\n\"" + e.getMessage() + "\"");
                                                        dialog.create().show();
                                                    }
                                                });
                                    }
                                })
                                // If update display name failed
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Log.e(Constants.REGISTER_ERROR, "Error updating display name", e);

                                        progressDialog.dismiss();

                                        // Show dialog with error info
                                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                                        dialog.setupOKDialog("Registration Failed",
                                                "An error occurred during your account setup. Please try register again!");
                                        dialog.create().show();
                                    }
                                });
                    }
                })
                // If upload failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e(Constants.REGISTER_ERROR, "Error uploading profile picture", e);

                        progressDialog.dismiss();

                        // Show dialog with error info
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Registration Failed",
                                "An error occurred during your account setup. Please try register again!");
                        dialog.create().show();
                    }
                });
    }

    /**
     * Add default notebook for user
     * @param userDoc       user document
     * @param firebaseUser  Firebase user
     */
    private void addDefaultNotebook(DocumentReference userDoc, FirebaseUser firebaseUser) {
        // Create new notebook and set title
        Notebook defaultNotebook = new Notebook();
        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

        // Get User default notebook document
        DocumentReference userDefNotebookDoc = userDoc.collection("notebooks").document(firebaseUser.getUid());

        // Add default notebook
        userDefNotebookDoc.set(defaultNotebook)
                // If add default notebook successful
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Add welcome note for user
                        addWelcomeNote(userDefNotebookDoc, firebaseUser);
                    }
                })
                // If add default notebook failed
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progressDialog.dismiss();

                        Log.e(Constants.REGISTER_ERROR, "Error adding default notebook", e);

                        // Show dialog with error info
                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        dialog.setupOKDialog("Registration Failed",
                                "An error occurred during your account setup. Please try register again!");
                        dialog.create().show();
                    }
                });
    }

    /**
     * Add welcome note for user
     * @param userDefNotebookDoc    user default notebook document
     * @param firebaseUser          Firebase user
     */
    private void addWelcomeNote(DocumentReference userDefNotebookDoc, FirebaseUser firebaseUser) {
        /* Create new notes and set title and content */
        Note welcomeNote = new Note();
        welcomeNote.setType(1);
        welcomeNote.setTitle(Constants.WELCOME_NOTE_TITLE);
        welcomeNote.setContent(Constants.WELCOME_NOTE_CONTENT);

        Note welcomeNote2 = new Note();
        welcomeNote2.setType(1);
        welcomeNote2.setTitle("Test note");
        welcomeNote2.setContent("I don't know what you did, Fry, but once again, you screwed up! Now all the planets are gonna start cracking wise about our mamas. When will that be? Uh, is the puppy mechanical in any way? She also liked to shut up!\n" +
                "\n" +
                "Who am I making this out to? Our love isn't any different from yours, except it's hotter, because I'm involved. Okay, it's 500 dollars, you have no choice of carrier, the battery can't hold the charge and the reception isn't very…");

        Note welcomeNote3 = new Note();
        welcomeNote3.setType(1);
        welcomeNote3.setTitle("Another test note but is intentionally set to exceed two lines title to simulate long title notes");
        welcomeNote3.setContent("When I was first asked to make a film about my nephew, Hubert Farnsworth, I thought \"Why should I?\" Then later, Leela made the film. But if I did make it, you can bet there would have been more topless women on motorcycles. Roll film! You are the last hope of the universe.");

        // User Note Collection
        CollectionReference userNoteCollection = userDefNotebookDoc.collection("notes");

        // Add Welcome Note
        userNoteCollection.add(welcomeNote)
                // If add welcome note successful
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();

                        // Send user verification email
                        firebaseUser.sendEmailVerification();

                        Bundle regData = new Bundle();
                        regData.putString("regEmail", regEmail);

                        // navigate to Register Successful page
                        NavHostFragment.findNavController(RegisterFragment02.this)
                                .navigate(R.id.action_registerFragment02_to_registerFragment03, regData);
                    }
                })
                // If add welcome note failed
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
}
