/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp;

import android.text.TextUtils;

import com.group6.noteapp.util.ValidationUtils;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
        mockStatic(TextUtils.class);

        when(TextUtils.isEmpty(ArgumentMatchers.any(CharSequence.class))).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                String string = (String) args[0];
                return (string == null || string.length() == 0);
            }
        });

        String testEmail = "";
        int result = ValidationUtils.validateEmail(testEmail);

//        ValidationUtils validationUtilsMock = Mockito.mock(ValidationUtils.class);
//        when(validationUtilsMock.validateEmail(anyString())).thenReturn(1);

        assertEquals(1, result);

//        ValidationUtils mockedValidationUtils = Mockito.mock(ValidationUtils.class);
////        PowerMockito.mockStatic(ValidationUtils.class);
//        when(ValidationUtils.validateEmail(anyString())).thenReturn(mockedValidationUtils);
    }
}
