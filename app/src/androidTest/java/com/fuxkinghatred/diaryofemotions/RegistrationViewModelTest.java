package com.fuxkinghatred.diaryofemotions;

import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fuxkinghatred.diaryofemotions.viewmodels.RegistrationViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class RegistrationViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockFirebaseUser;
    @Mock
    private Task<Void> mockVoidTask;
    @Mock
    private Observer<String> errorObserver;
    @Mock
    private Observer<FirebaseUser> userObserver;

    private RegistrationViewModel viewModel;
    private static final String TEST_EMAIL = "test@test.ru";
    private static final String TEST_PASSWORD = "password";
    private static final String TEST_NAME = "Test User";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new RegistrationViewModel();
        viewModel.setAuth(mockAuth);
        viewModel.getError().observeForever(errorObserver);
        viewModel.getUser().observeForever(userObserver);
        when(mockAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
        when(mockAuth.createUserWithEmailAndPassword(anyString(), anyString()))
                .thenReturn(Tasks.forResult(mock(AuthResult.class)));
        when(mockFirebaseUser.updateProfile(any())).thenReturn(mockVoidTask);
    }

    @Test
    public void register_userNotCreated() {
        when(mockAuth.getCurrentUser()).thenReturn(null);
        viewModel.register(TEST_EMAIL, TEST_PASSWORD, TEST_NAME);
        verify(mockAuth).createUserWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);
        verify(mockFirebaseUser, never()).updateProfile(any());
        verify(userObserver, never()).onChanged(mockFirebaseUser);
    }

    @Test
    public void authStateChange_userNull() {
        when(mockAuth.getCurrentUser()).thenReturn(null);
        viewModel = new RegistrationViewModel();
        viewModel.setAuth(mockAuth);
        viewModel.getUser().observeForever(userObserver);
        verify(userObserver, atLeastOnce()).onChanged(null);
    }
}