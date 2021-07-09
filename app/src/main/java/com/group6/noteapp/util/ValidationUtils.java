/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class ValidationUtils {
    private final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])" +
            "(?=.*\\d)(?=.*[$&+,:;=?@#|'<>.^*()%!-])[A-Za-z\\d$&+,:;=?@#|'<>.^*()%!-]{8,}$";
    private final String fullNameRegex = "^([a-zA-Zàáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉị" +
            "òóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢ" +
            "ÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ]{2,})(\\s[a-zA-Zàáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉị" +
            "òóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢ" +
            "ÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ]{2,})+$";
    private final String emailRegex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
            "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
            "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";
    private final String phoneRegex = "^(0[1-9][0-9]{8})|(\\+84[1-9][0-9]{8})$";
    private final SimpleDateFormat dateFormatter;   // date formatter

    /**
     * Constructor
     */
    public ValidationUtils(){
        this.dateFormatter = new SimpleDateFormat("dd/MM/yyyy");    // instantiate with pattern
        dateFormatter.setLenient(false);                                   // set lenient to false
    }

    /**
     * Validate if username input is valid
     * @param username username to validate
     * @return  0 if valid
     *          1 if empty
     *          2 if duplicate
     */
    public int validateUsername(String username){
        if (TextUtils.isEmpty(username)) {
            return 1;
        }

        return 0;
    }

    /**
     * Validate if password input is valid
     * @param password password to validate
     * @param confirmPassword confirm password to validate
     * @return  0 if valid
     *          1 if password is empty
     *          2 if password doesn't match regex
     *          3 if confirm password doesn't match password
     */
    public int validatePassword(String password, String confirmPassword){
        if (TextUtils.isEmpty(password)) {
            return 1;
        } else if (!password.matches(passwordRegex)) {
            return 2;
        } else if (!password.equals(confirmPassword)) {
            return 3;
        }

        return 0;
    }

    /**
     * Validate if full name input is valid
     * @param fullName full name to validate
     * @return  0 if valid
     *          1 if empty
     *          2 if doesn't match regex
     */
    public int validateFullName(String fullName){
        if (TextUtils.isEmpty(fullName)) {
            return 1;
        } else if (!fullName.matches(fullNameRegex)) {
            return 2;
        }

        return 0;
    }

    /**
     * Validate if email input is valid
     * @param email email to validate
     * @return  0 if valid
     *          1 if isn't empty AND doesn't match regex
     */
    public int validateEmail(String email){
        if (!TextUtils.isEmpty(email) && !email.matches(emailRegex)) {
            return 1;
        }

        return 0;
    }

    /**
     * Validate if phone input is valid
     * @param phone phone to validate
     * @return  0 if valid
     *          1 if isn't empty AND doesn't match regex
     */
    public int validatePhone(String phone){
        if (!TextUtils.isEmpty(phone) && !phone.matches(phoneRegex)) {
            return 1;
        }

        return 0;
    }

    /**
     * Validate if birth date input is valid
     * @param birthDate birth date to validate
     * @return  0 if valid
     *          1 if isn't in dd/MM/yyyy format
     *          2 if date is after today
     */
    public int validateBirthdate(String birthDate){
        // if birth date is empty, mark it as valid
        if (TextUtils.isEmpty(birthDate)){
            return 0;
        }

        Date parsedBirthDate;   // the parsed birth date

        // try parse the birth date to dd/MM/yyyy format
        try{
            parsedBirthDate = dateFormatter.parse(birthDate);
        } catch (ParseException e){
            // if not successful
            return 1;
        }

        // if birth date is after today
        if (parsedBirthDate.after(new Date())){
            return 2;
        }

        return 0;
    }
}
