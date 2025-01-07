package com.fuxkinghatred.diaryofemotions.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * ViewModel для управления процессом аутентификации пользователя.
 */
public class LoginViewModel extends ViewModel {
    /**
     * Тег для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_LOGIN_VIEW_MODEL;
    /**
     * Экземпляр FirebaseAuth для взаимодействия с Firebase Authentication.
     */
    private FirebaseAuth auth;
    /**
     * LiveData, хранящая сообщение об ошибке аутентификации.
     */
    private final MutableLiveData<String> error = new MutableLiveData<>();
    /**
     * LiveData, хранящая текущего аутентифицированного пользователя.
     */
    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>(null);
    /**
     * LiveData, хранящая флаг, указывающий на успешную отправку письма для восстановления пароля.
     */
    private final MutableLiveData<Boolean> passwordResetSent = new MutableLiveData<>();

    /**
     * Возвращает LiveData с сообщением об ошибке.
     *
     * @return LiveData с сообщением об ошибке.
     */
    public LiveData<String> getError() {
        return error;
    }

    /**
     * Возвращает LiveData с текущим аутентифицированным пользователем.
     *
     * @return LiveData с текущим аутентифицированным пользователем.
     */
    public LiveData<FirebaseUser> getUser() {
        return user;
    }

    /**
     * Устанавливает авторизацию FirebaseAuth.
     *
     * @param auth FirebaseAuth
     */
    public void setAuth(FirebaseAuth auth) {
        this.auth = auth;
        auth.addAuthStateListener(firebaseAuth -> {
            Log.d(
                    TAG,
                    "LoginViewModel" +
                            "user - " + user
            );
            user.setValue(firebaseAuth.getCurrentUser());
        });
    }

    /**
     * Возвращает LiveData с флагом, указывающим на успешную отправку письма для восстановления пароля.
     *
     * @return LiveData с флагом успешной отправки письма для восстановления пароля.
     */
    public LiveData<Boolean> getPasswordResetSent() {
        return passwordResetSent;
    }

    /**
     * Конструктор класса.
     * Инициализирует FirebaseAuth и устанавливает слушатель изменения состояния аутентификации.
     * При изменении состояния аутентификации (вход/выход) обновляет LiveData с текущим пользователем.
     */
    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> {
            Log.d(
                    TAG,
                    "LoginViewModel" +
                            "user - " + user
            );
            user.setValue(firebaseAuth.getCurrentUser());
        });
    }

    /**
     * Осуществляет вход пользователя в систему с помощью email и пароля.
     * В случае неудачи устанавливает сообщение об ошибке в LiveData.
     *
     * @param email    Email пользователя.
     * @param password Пароль пользователя.
     */
    public void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.setValue(auth.getCurrentUser());
                    } else {
                        // Отправляем код ошибки
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthException) {
                            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                            error.setValue(errorCode);
                        } else {
                            error.setValue("Произошла ошибка");
                        }
                    }
                });
    }

    /**
     * Отправляет письмо для восстановления пароля на указанный email.
     * В случае успешной отправки устанавливает флаг успешной отправки в LiveData.
     * В случае ошибки устанавливает сообщение об ошибке в LiveData.
     * Если пользователь с таким email не найден, устанавливает соответствующее сообщение об ошибке.
     *
     * @param email Email пользователя для восстановления пароля.
     */
    public void forgotPassword(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordResetSent.setValue(true);
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof FirebaseAuthException) {
                            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
                            error.setValue(errorCode);
                        } else {
                            error.setValue("Произошла ошибка");
                        }

                    }
                });
    }
}