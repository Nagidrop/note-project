/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.controller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.group6.noteapp.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    TextInputEditText inputRegBirthdateEditText;

    public DatePickerFragment(TextInputEditText inputRegBirthdateEditText){
        this.inputRegBirthdateEditText = inputRegBirthdateEditText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(),
                R.style.datePickerTheme, this, year, month, day);
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return pickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        inputRegBirthdateEditText.setText(day + "/" + (month + 1) + "/" + year);
    }
}