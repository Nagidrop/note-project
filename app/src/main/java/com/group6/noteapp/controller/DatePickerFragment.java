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

/**
 * Fragment to open Date Picker Dialog inside fragment
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    TextInputEditText inputRegBirthdateEditText;    // the birthdate Input EditText

    /**
     * Constructor
     * @param inputRegBirthdateEditText the birthdate Input EditText
     */
    public DatePickerFragment(TextInputEditText inputRegBirthdateEditText){
        this.inputRegBirthdateEditText = inputRegBirthdateEditText;
    }

    @Override
    /**
     * Create Date Picker dialog
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(),
                R.style.datePickerTheme, this, year, month, day);

        // Set maximum date allowed to select as current date
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        return pickerDialog;
    }

    /**
     * When the date is set, the birthdate input is also set with dd/MM/yyyy format
     * @param view
     * @param year
     * @param month
     * @param day
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        inputRegBirthdateEditText.setText(day + "/" + (month + 1) + "/" + year);
    }
}