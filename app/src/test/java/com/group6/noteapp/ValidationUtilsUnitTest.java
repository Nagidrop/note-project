/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp;

import com.group6.noteapp.util.ValidationUtils;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ValidationUtilsUnitTest extends TestCase {
    @Before
    public void setUp(){
//        PowerMockito.mockStatic(ValidationUtils.class);
//        when(ValidationUtils.getInstance()).thenReturn(mockedValidationUtils);
//        ValidationUtils mockedValidationUtils = Mockito.mock(FirebaseDatabase.class);
//        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
    }

    @Test
    public void testValidateEmail(){
        String email = "";
        ValidationUtils validationUtilsMock = Mockito.mock(ValidationUtils.class);
        when(validationUtilsMock.validateEmail(anyString())).thenReturn(1);

        assertEquals(1, validationUtilsMock.validateEmail(email));

//        ValidationUtils mockedValidationUtils = Mockito.mock(ValidationUtils.class);
////        PowerMockito.mockStatic(ValidationUtils.class);
//        when(ValidationUtils.validateEmail(anyString())).thenReturn(mockedValidationUtils);
    }
}
