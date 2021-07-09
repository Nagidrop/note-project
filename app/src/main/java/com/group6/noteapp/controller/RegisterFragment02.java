package com.group6.noteapp.controller;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.group6.noteapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private String mParam1;
    private String mParam2;

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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        inflatedView = inflater.inflate(R.layout.fragment_register02, container, false);
//        MaterialButton btnNext = inflatedView.findViewById(R.id.btnNext01);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View v) {
//                /* Get EditText Views */
//
//                TextInputLayout etFullname = inflatedView.findViewById(R.id.textInputRegFullname);
//                TextInputLayout etBirthday = inflatedView.findViewById(R.id.textInputRegBirthday);
//                TextInputLayout etAddress = inflatedView.findViewById(R.id.textInputRegAddress);
//
//                /* Create local variables to store the EditText Views' current values */
//
//                String regFullname = etFullname.getEditText().getText().toString();
//                String regBirthday = etBirthday.getEditText().getText().toString();
//                String regAddress = etAddress.getEditText().getText().toString();
//            }
//        });

        return inflater.inflate(R.layout.fragment_register02, container, false);
    }
}