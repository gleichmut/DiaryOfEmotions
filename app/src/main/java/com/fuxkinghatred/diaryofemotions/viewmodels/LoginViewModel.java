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
 * ViewModel для LoginActivity.
 */
public class LoginViewModel extends ViewModel {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_LOGIN_VIEW_MODEL;
    /**
     * Экземпляр FirebaseAuth для взаимодействия с Firebase Authentication.
     */
    private FirebaseAuth auth;
    /**
     * LiveData ошибок аутентификации.
     */
    private final MutableLiveData<String> error = new MutableLiveData<>();
    /**
     * LiveData текущего аутентифицированного пользователя.
     */
    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>(null);
    /**
     * LiveData флага успешности отправки письма для восстановления пароля.
     */
    private final MutableLiveData<Boolean> passwordResetSent = new MutableLiveData<>();

    /**
     * Возвращает LiveData сообщения об ошибке.
     *
     * @return LiveData сообщения об ошибке.
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
     * Возвращает LiveData флага успешности отправки письма для восстановления пароля.
     *
     * @return LiveData флага успешности отправки письма для восстановления пароля.
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
     * @param email    почта пользователя.
     * @param password пароль пользователя.
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
     * Отправляет письмо для восстановления пароля на указанную почту.
     * В случае успешной отправки устанавливает флаг успешной отправки в LiveData.
     * В случае ошибки устанавливает сообщение об ошибке в LiveData.
     * Если пользователь с такой почтой не найден, устанавливает соответствующее сообщение об ошибке.
     *
     * @param email почта пользователя для восстановления пароля.
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