/**
 * Quan Duc Loc CE140037
 */
package com.group6.noteapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group6.noteapp.controller.LoginActivity;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class})
public class LoginUnitTest extends TestCase {
    private DatabaseReference mockedDatabaseReference;
    private FirebaseAuth mockedFirebaseAuth;
//    private DatabaseReference mockedDatabaseReference;
    private FirebaseUser mockedFirebaseUser;

    @Before
    public void setUp() {
        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

//        FirebaseAuth mockedFirebaseAuth = Mockito.mock(FirebaseAuth.class);
//        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(mockedFirebaseUser);

        PowerMockito.mockStatic(FirebaseAuth.class);
        when(FirebaseAuth.getInstance()).thenReturn(mockedFirebaseAuth);

        PowerMockito.mockStatic(FirebaseUser.class);
        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(mockedFirebaseUser);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
    }

    @Test
    public void onCreateTest(){
        mockedFirebaseUser = null;

        when(mockedFirebaseAuth.getCurrentUser()).thenReturn(mockedFirebaseUser);


    }

    @Test
    public void getSignedInUserProfileTest() {
//        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);

//        doAnswer(new Answer<Void>() {
//            @Override
//            public Void answer(InvocationOnMock invocation) throws Throwable {
//                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];
//
//                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
//                //when(mockedDataSnapshot.getValue(User.class)).thenReturn(testOrMockedUser)
//
//                valueEventListener.onDataChange(mockedDataSnapshot);
//                //valueEventListener.onCancelled(...);
//
//                return null;
//            }
//        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        new LoginActivity();

        // check preferences are updated
    }
}
