/*
 * Group 06 SE1402
 */

package com.group6.noteapp.util;

import android.text.TextUtils;

/**
 * Utils for validating input fields
 */
public final class ValidationUtils {
    private static final String passwordRegex = "^[A-Za-z\\d$&+,:;=?@#|'<>.^*()%!-]{8,}$";  // minimum 8 characters
    private static final String fullNameRegex = "^([a-zA-Zàáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉị" +
            "òóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢ" +
            "ÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ]+)" +   // match a single Vietnamese word
            "(\\s[a-zA-Zàáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉị" +
            "òóõọỏôốồổỗộơớờởỡợùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢ" +
            "ÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ]+)+$"   // follows by a single Vietnamese word or more
            ;
    private static final String emailRegex = "^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*" +
            "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
            "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?" +
            "|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])$"
            ;                           // conform to RFC 5322
    private static final String addressRegex = "^[a-zA-Zàáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợ" +
            "ùúũụủưứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ" +
            "0-9.\\-\\s,/]*$";    // match a Vietnamese or foreign country's address

    /**
     * Validate if email input is valid
     * @param email email string to validate
     * @return  0 if valid
     *          1 if isn't empty
     *          2 if doesn't match regex
     */
    public static int validateEmail(String email){
        if (TextUtils.isEmpty(email)) {
            return 1;
        } else if (!email.matches(emailRegex)){
            return 2;
        }

        return 0;
    }

    /**
     * Validate if password input is valid
     * @param password password string to validate
     * @param confirmPassword confirm password string to validate
     * @return  0 if valid
     *          1 if password is empty
     *          2 if password doesn't match regex
     *          3 if confirm password doesn't match password
     */
    public static int validatePasswordReg(String password, String confirmPassword){
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
     * Validate if password input is valid
     * @param password password string to validate
     * @return  0 if valid
     *          1 if password is empty
     */
    public static int validatePasswordLog(String password){
        if (TextUtils.isEmpty(password)) {
            return 1;
        }

        return 0;
    }

    /**
     * Validate if full name input is valid
     * @param fullName full name string to validate
     * @return  0 if valid
     *          1 if empty
     *          2 if doesn't match regex
     */
    public static int validateFullName(String fullName){
        if (TextUtils.isEmpty(fullName)) {
            return 1;
        } else if (!fullName.matches(fullNameRegex)) {
            return 2;
        }

        return 0;
    }

    /**
     * Validate if address input is valid
     * @param address address string to validate
     * @return  0 if valid
     *          1 if doesn't match regex
     */
    public static int validateAddress(String address){
        if (!address.matches(addressRegex)){
            return 1;
        }

        return 0;
    }

    /**
     * Validate if file name input is valid
     * @param fileName file name string to validate
     * @return  0 if valid
     *          1 if empty
     */
    public static int validateFileName(String fileName){
        fileName = fileName.trim();

        if (TextUtils.isEmpty(fileName)){
            return 1;
        }

        return 0;
    }
}
