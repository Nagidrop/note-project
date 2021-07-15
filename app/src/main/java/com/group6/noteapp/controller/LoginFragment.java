package com.group6.noteapp.controller;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 696969; // google sign in number
    private static final String TAG = "LoginFragment"; // Tag for logging

    private MaterialButton btnLogin; // login button
    private TextInputLayout inputLogEmail; // inputted email
    private TextInputLayout inputLogPassword; // inputted password
    private FirebaseAuth firebaseAuth; // Firebase auth
    private LoginButton loginButton; // Login button
    private CallbackManager callbackManager; // Facebook Callback manager
    private NoteAppProgressDialog progressDialog; // Progress dialog
    private GoogleSignInClient mGoogleSignInClient; // Google sign in client
    private FirebaseFirestore db; // Firestore database

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Initialize database and login instance
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Google Sign In
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        // Get google sign in client
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // get firestore instance
        db = FirebaseFirestore.getInstance();
        // get firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Initialize login function
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_login, container, false);


        // Get register button
        MaterialButton btnRegister = inflatedView.findViewById(R.id.btnNoAccount);
        // Set navigate to register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_registerFragment01);
            }
        });

        // To forgot password fragment
        MaterialButton btnForgotPassword = inflatedView.findViewById(R.id.btnForgotPassword);
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_loginFragment_to_forgotPasswordFragment01);
            }
        });

        // Get and set login Facebook button
        MaterialButton btnLoginFacebook = inflatedView.findViewById(R.id.btnLoginFacebook);
        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show progress dialog
                progressDialog = new NoteAppProgressDialog(getActivity());
                progressDialog.setUpDialog("Just a moment...",
                        "Please wait while we connect you to Note App.");
                progressDialog.show();

                // button facebook login perform click
                loginButton.performClick();
            }
        });

        //get TextInputlayout
        inputLogEmail = inflatedView.findViewById(R.id.txtInputLoginEmail);
        inputLogPassword = inflatedView.findViewById(R.id.txtInputLoginPassword);

        //Get login button
        btnLogin = inflatedView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check login credentials
                checkCredentials();
            }
        });

//        progressDialog = new ProgressDialog(getActivity());
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) inflatedView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            /**
             * on facebook login success
             * @param loginResult
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(LoginFragment.this,"login successful!", Toast.LENGTH_LONG).show();

                // Handle facebook access token
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            /**
             * on facebook login cancel
             */
            @Override
            public void onCancel() {
                // Toast login cancel
                Toast.makeText(getActivity(), "Login cancel !", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            /**
             * On facebook login error
             * @param exception
             */
            @Override
            public void onError(FacebookException exception) {
                // Toast login error details
                Toast.makeText(getActivity(), "Login Error: " + exception.getMessage(), Toast.LENGTH_LONG)
                        .show();
                progressDialog.dismiss();
            }
        });

        // ----------------------------------
        // Login Google
        // ----------------------------------
        // Button listeners
        MaterialButton btnGoogleLogin = inflatedView.findViewById(R.id.btnLoginGoogle);
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * sign In google
             * @param v
             */
            @Override
            public void onClick(View v) {
                progressDialog = new NoteAppProgressDialog(getActivity());
                progressDialog.setUpDialog("Just a moment...",
                        "Please wait while we connect you to Note App.");
                progressDialog.show();

                signIn();
            }
        });


        return inflatedView;
    }

    /**
     * Check user login credentials
     */
    private void checkCredentials() {
        String logEmail = inputLogEmail.getEditText().getText().toString();
        String logPassword = inputLogPassword.getEditText().getText().toString();

        inputLogEmail.setErrorEnabled(false);
        inputLogPassword.setErrorEnabled(false);

        inputLogEmail.setErrorEnabled(true);
        inputLogPassword.setErrorEnabled(true);

        boolean isInputValid = true;
        int emailValidateResult = ValidationUtils.validateEmail(logEmail);
        int passwordValidateResult = ValidationUtils.validatePasswordLog(logPassword);

        if (emailValidateResult == 1) {
            isInputValid = false;
            inputLogEmail.setError("Email must not be empty.");
        } else if (emailValidateResult == 2) {
            isInputValid = false;
            inputLogEmail.setError("Please use a valid email. (Ex: abc@g.cn)");
        }

        if (passwordValidateResult == 1) {
            isInputValid = false;
            inputLogPassword.setError("Password must not be empty.");
        }

        if (isInputValid) {
            progressDialog = new NoteAppProgressDialog(getActivity());
            progressDialog.setUpDialog("Just a moment...",
                    "Please wait while we connect you to Note App.");
            progressDialog.show();

            loginWithEmailAndPassword(logEmail, logPassword);
        }
    }

    /**
     * Login to firebase with email and password
     * @param email user email
     * @param password user password
     */
    public void loginWithEmailAndPassword(String email, String password) {
        // Firebase sign in with email and password
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();

                        if (authResult.getUser().isEmailVerified()) {
                            goToMainActivity();
                        } else {
                            NoteAppDialog dialog = new NoteAppDialog(getActivity());
                            dialog.setupOKDialog("Login Failed",
                                    "Please verify your email address before logging in!");
                            dialog.create().show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e(Constants.LOGIN_ERROR, "Login error", e);

                        NoteAppDialog dialog = new NoteAppDialog(getActivity());
                        switch (((FirebaseAuthException) e).getErrorCode()) {
                            case "ERROR_USER_NOT_FOUND":
                            case "ERROR_WRONG_PASSWORD":
                                inputLogEmail.setError(" ");
                                inputLogPassword.setError("Your email or password is incorrect.");

                                break;

                            case "ERROR_USER_DISABLED":
                                dialog.setupOKDialog("Login Failed",
                                        "Your account has been disabled by an admin!");
                                dialog.create().show();

                                break;

                            case "ERROR_USER_TOKEN_EXPIRED":
                                dialog.setupOKDialog("Login Failed",
                                        "Your credentials has been changed. Please log in again!");
                                dialog.create().show();

                                break;

                            default:
                                dialog.setupOKDialog("Login Failed",
                                        "Sorry, an unexpected error occurred. Please try log in again!");
                                dialog.create().show();

                                break;
                        }

                        progressDialog.dismiss();
                    }
                });
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
    }
    // [END on_start_check_user]

    /**
     * handle activity result for login facebook and google
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // if request code is RC_SIGN_IN
        // handle login with google
        // else handle login with facebook
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        } else {
            // Facebook get ressult
            callbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }

    /**
     * To Main activity
     */
    private void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Handle Facebook access token
     * @param token facebook access token
     */
    private void handleFacebookAccessToken(AccessToken token) {

        // Get auth credential from facebook auth provider
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        // sign in to firebase with credential
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                    /**
                     * Handle login complete check user exist in database
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            String Uid = user.getUid();

                            // Check user exist in database
                            DocumentReference userInfoDoc = db.collection("users").document(Uid);
                            userInfoDoc.get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(
                                                @NonNull @NotNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                // If user exist then go to main
                                                // else create new user info
                                                if (document.exists()) {
                                                    progressDialog.dismiss();
                                                    goToMainActivity();
                                                }
                                                else{
                                                    setUpUserInfo(user, userInfoDoc);
                                                }
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("error", task.getException().getMessage(), task.getException());
                        }
                    }
                });
    }

    /**
     * Handle firebase login with google
     * @param idToken
     */
    private void firebaseAuthWithGoogle(String idToken) {

        // Get credential from google auth provider
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        // Sign in with credential
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    /**
                     * Handle login complete check user exist in database
                     * @param task
                     */
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            String Uid = user.getUid();

                            // Check user exist in database
                            DocumentReference userInfoDoc = db.collection("users").document(Uid);
                            userInfoDoc.get().addOnCompleteListener(
                                    new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(
                                                @NonNull @NotNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                // If user exist then go to main
                                                // else create new user info
                                                if (document.exists()) {
                                                    progressDialog.dismiss();
                                                    goToMainActivity();
                                                }else{
                                                    setUpUserInfo(user, userInfoDoc);
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }


    /**
     * Start google signIn intent
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Add default notebook for user
     * @param userInfoDoc   user document
     * @param firebaseUser  Firebase user
     */
    private void addDefaultNotebook(DocumentReference userInfoDoc, FirebaseUser firebaseUser) {
        // Create new notebook and set title
        Notebook defaultNotebook = new Notebook();
        defaultNotebook.setTitle(Constants.FIRST_NOTEBOOK_NAME);

        // Get User default notebook document
        DocumentReference userDefNotebookDoc = userInfoDoc.collection("notebooks").document(firebaseUser.getUid());

        // Add default notebook
        userDefNotebookDoc.set(defaultNotebook)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    // If add default notebook successful
                    @Override
                    public void onSuccess(Void unused) {
                        // Add welcome note
                        addWelcomeNote(userDefNotebookDoc, firebaseUser);
                    }
                })
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
        Note welcomeNote = new Note();
        welcomeNote.setTitle(Constants.WELCOME_NOTE_TITLE);
        welcomeNote.setContent(Constants.WELCOME_NOTE_CONTENT);

        Note welcomeNote2 = new Note();
        welcomeNote2.setTitle("Test note - delete at release");
        welcomeNote2.setContent(
                "I don't know what you did, Fry, but once again, you screwed up! Now all the planets are gonna start cracking wise about our mamas. When will that be? Uh, is the puppy mechanical in any way? She also liked to shut up!\n" +
                        "\n" +
                        "Who am I making this out to? Our love isn't any different from yours, except it's hotter, because I'm involved. Okay, it's 500 dollars, you have no choice of carrier, the battery can't hold the charge and the reception isn't very…");

        Note welcomeNote3 = new Note();
        welcomeNote3.setTitle(
                "Test note but intentionally exceeds longer than two lines title - delete at release");
        welcomeNote3.setContent(
                "When I was first asked to make a film about my nephew, Hubert Farnsworth, I thought \"Why should I?\" Then later, Leela made the film. But if I did make it, you can bet there would have been more topless women on motorcycles. Roll film! You are the last hope of the universe.");

        CollectionReference userNoteCollection = userDefNotebookDoc.collection("notes");
        userNoteCollection.add(welcomeNote)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Add welcome Note success");
                        goToMainActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                        Log.e(Constants.REGISTER_ERROR, "Error adding welcome note", e);
                    }
                });

        userNoteCollection.add(welcomeNote2);
        userNoteCollection.add(welcomeNote3);
    }

    /**
     * Set up new user info
     * @param firebaseUser
     * @param userInfoDoc
     */
    private void setUpUserInfo(FirebaseUser firebaseUser, DocumentReference userInfoDoc) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri profilePic = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + getResources().getResourcePackageName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceTypeName(R.drawable.img_profile_pic)
                + '/' + getResources().getResourceEntryName(R.drawable.img_profile_pic));

        final StorageReference profilePictureRef =
                storageRef.child(firebaseUser.getUid() +  "/images/" + "profilePicture.png");

        profilePictureRef.putFile(profilePic)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        User newUser = new User();
                        newUser.setFullName(firebaseUser.getDisplayName());

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
