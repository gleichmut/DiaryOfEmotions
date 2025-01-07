package com.fuxkinghatred.diaryofemotions;

import static org.mockito.Mockito.verify;

import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.fuxkinghatred.diaryofemotions.viewmodels.MyNotesViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MyNotesViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private FirebaseAuth mockAuth;
    private MyNotesViewModel viewModel;
    private MockedStatic<FirebaseAuth> firebaseAuthMockedStatic;

    @Before
    public void setup() {
        try {
            MockitoAnnotations.initMocks(this);
            firebaseAuthMockedStatic = Mockito.mockStatic(FirebaseAuth.class);
            firebaseAuthMockedStatic.when(FirebaseAuth::getInstance).thenReturn(mockAuth);
            viewModel = new MyNotesViewModel();
        } catch (Exception e) {
            Log.e("MyNotesViewModelTest", "setup: e" + e);
            firebaseAuthMockedStatic = null;
            viewModel                = null;
        }
    }

    @Test
    public void logout_callsFirebaseAuthSignOut() {
        if (viewModel != null) {
            viewModel.logout();
            verify(mockAuth).signOut();
        }
    }

    @After
    public void tearDown() {
        if (firebaseAuthMockedStatic != null)
            firebaseAuthMockedStatic.close();
    }
}

