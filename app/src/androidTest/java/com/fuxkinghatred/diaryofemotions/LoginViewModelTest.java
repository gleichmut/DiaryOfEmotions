package com.fuxkinghatred.diaryofemotions;

import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.fuxkinghatred.diaryofemotions.viewmodels.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class LoginViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockFirebaseUser;
    @Mock
    private Task<AuthResult> mockAuthTask;
    @Mock
    private Task<Void> mockVoidTask;
    @Mock
    private Observer<String> errorObserver;
    @Mock
    private Observer<FirebaseUser> userObserver;
    @Mock
    private Observer<Boolean> passwordResetObserver;
    @Captor
    private ArgumentCaptor<OnCompleteListener<AuthResult>> authResultCaptor;
    @Captor
    private ArgumentCaptor<OnCompleteListener<Void>> voidResultCaptor;
    private LoginViewModel viewModel;
    private static final String TEST_EMAIL = "test@test.ru";
    private static final String TEST_PASSWORD = "password";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new LoginViewModel();
        viewModel.setAuth(mockAuth);
        viewModel.getError().observeForever(errorObserver);
        viewModel.getUser().observeForever(userObserver);
        viewModel.getPasswordResetSent().observeForever(passwordResetObserver);
        Mockito.when(mockAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
    }

    @Test
    public void login_success() {
        Mockito.when(mockAuthTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockAuthTask.getResult()).thenReturn(Mockito.mock(AuthResult.class));
        Mockito.when(mockAuth.signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD)).thenReturn(mockAuthTask);
        viewModel.login(TEST_EMAIL, TEST_PASSWORD);
        Mockito.verify(mockAuth).signInWithEmailAndPassword(TEST_EMAIL, TEST_PASSWORD);
        Mockito.verify(mockAuthTask).addOnCompleteListener(authResultCaptor.capture());
        authResultCaptor.getValue().onComplete(mockAuthTask);
        Mockito.verify(userObserver).onChanged(mockFirebaseUser);
        Mockito.verify(errorObserver, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void forgotPassword_success() {
        Mockito.when(mockVoidTask.isSuccessful()).thenReturn(true);
        Mockito.when(mockAuth.sendPasswordResetEmail(TEST_EMAIL)).thenReturn(mockVoidTask);
        viewModel.forgotPassword(TEST_EMAIL);
        Mockito.verify(mockAuth).sendPasswordResetEmail(TEST_EMAIL);
        Mockito.verify(mockVoidTask).addOnCompleteListener(voidResultCaptor.capture());
        voidResultCaptor.getValue().onComplete(mockVoidTask);
        Mockito.verify(passwordResetObserver).onChanged(true);
        Mockito.verify(errorObserver, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void forgotPassword_failure() {
        String errorCode = "ERROR_USER_NOT_FOUND";
        FirebaseAuthException exception = new FirebaseAuthException(errorCode, "User not found");
        Mockito.when(mockVoidTask.isSuccessful()).thenReturn(false);
        Mockito.when(mockVoidTask.getException()).thenReturn(exception);
        Mockito.when(mockAuth.sendPasswordResetEmail(TEST_EMAIL)).thenReturn(mockVoidTask);
        viewModel.forgotPassword(TEST_EMAIL);
        Mockito.verify(mockAuth).sendPasswordResetEmail(TEST_EMAIL);
        Mockito.verify(mockVoidTask).addOnCompleteListener(voidResultCaptor.capture());
        voidResultCaptor.getValue().onComplete(mockVoidTask);
        Mockito.verify(errorObserver).onChanged(errorCode);
        Mockito.verify(passwordResetObserver, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void forgotPassword_genericError() {
        Mockito.when(mockVoidTask.isSuccessful()).thenReturn(false);
        Mockito.when(mockVoidTask.getException()).thenReturn(new Exception("Some Generic Error"));
        Mockito.when(mockAuth.sendPasswordResetEmail(TEST_EMAIL)).thenReturn(mockVoidTask);
        viewModel.forgotPassword(TEST_EMAIL);
        Mockito.verify(mockAuth).sendPasswordResetEmail(TEST_EMAIL);
        Mockito.verify(mockVoidTask).addOnCompleteListener(voidResultCaptor.capture());
        voidResultCaptor.getValue().onComplete(mockVoidTask);
        Mockito.verify(errorObserver).onChanged("Произошла ошибка");
        Mockito.verify(passwordResetObserver, Mockito.never()).onChanged(ArgumentMatchers.any());
    }

    @Test
    public void authStateChange_userNull() {
        when(mockAuth.getCurrentUser()).thenReturn(null);
        viewModel = new LoginViewModel();
        viewModel.setAuth(mockAuth);
        viewModel.getUser().observeForever(userObserver);
        verify(userObserver, atLeastOnce()).onChanged(null);
    }
}
