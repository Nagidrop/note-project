package com.group6.noteapp.controller;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.group6.noteapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ForgotPasswordFragment01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgotPasswordFragment01 extends Fragment {

    View inflatedView;

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
        MaterialButton btnSubmit = inflatedView.findViewById(R.id.btnForgotSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                NavHostFragment.findNavController(ForgotPasswordFragment01.this).navigate(R.id.action_forgotPasswordFragment01_to_registerFragment03);
            }
        });
        // Inflate the layout for this fragment
        return inflatedView;
    }
}