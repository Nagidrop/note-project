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
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment02#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment02 extends Fragment {
    private View inflatedView;
    private String email;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ForgotPasswordFragment02() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ForgotPasswordFragment02.
     */
    // TODO: Rename and change types and number of parameters
    public static ForgotPasswordFragment02 newInstance(String param1, String param2) {
        ForgotPasswordFragment02 fragment = new ForgotPasswordFragment02();
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
            email = getArguments().getString("email");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inflatedView = inflater.inflate(R.layout.fragment_forgot_password02, container, false);

        TextView txtForgotPassword = inflatedView.findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setText(Html.fromHtml(getString(R.string.email_sent_forgot_pass, email)));

        MaterialButton btnForgotLogin = inflatedView.findViewById(R.id.btnForgotLogin);
        btnForgotLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                NavHostFragment.findNavController(ForgotPasswordFragment02.this)
                        .navigate(R.id.action_forgotPasswordFragment02_to_loginFragment);
            }
        });

        // Inflate the layout for this fragment
        return inflatedView;
    }
}