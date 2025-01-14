package com.fuxkinghatred.diaryofemotions.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * ViewModel для RegistrationActivity.
 */
public class RegistrationViewModel extends ViewModel {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_REGISTRATION_VIEW_MODEL;
    /**
     * Экземпляр FirebaseAuth для взаимодействия с Firebase Authentication.
     */
    private FirebaseAuth auth;
    /**
     * LiveData ошибок регистрации.
     */
    private final MutableLiveData<String> error = new MutableLiveData<>();
    /**
     * LiveData текущего зарегистрированного пользователя.
     */
    private final MutableLiveData<FirebaseUser> user = new MutableLiveData<>();

    /**
     * Конструктор класса.
     * Инициализирует FirebaseAuth и устанавливает слушатель изменения состояния аутентификации.
     * При изменении состояния аутентификации (регистрация) обновляет LiveData с текущим пользователем.
     */
    public RegistrationViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(firebaseAuth -> user.setValue(firebaseAuth.getCurrentUser()));
    }

    /**
     * Возвращает LiveData сообщения об ошибке.
     *
     * @return LiveData сообщения об ошибке.
     */
    public LiveData<String> getError() {
        return error;
    }

    /**
     * Возвращает LiveData с текущим зарегистрированным пользователем.
     *
     * @return LiveData с текущим зарегистрированным пользователем.
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
    }

    /**
     * Регистрирует нового пользователя с помощью почты, пароля и имени пользователя.
     * После успешной регистрации обновляет профиль пользователя, устанавливая его имя.
     *
     * @param email    почта нового пользователя.
     * @param password пароль нового пользователя.
     * @param name     имя нового пользователя.
     */
    public void register(String email, String password, String name) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    if (firebaseUser == null)
                        return;

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build();

                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(updateTask ->
                    {
                        if (updateTask.isSuccessful()) {
                            Log.d(
                                    TAG,
                                    "register" +
                                            "User profile updated." + Objects.requireNonNull(profileUpdates.getDisplayName())
                            );
                        } else {
                            Log.e(
                                    TAG,
                                    "register" +
                                            "Error updating user profile."
                            );
                        }
                    });
                })
                .addOnFailureListener(e ->
                {
                    Log.e(
                            TAG,
                            "register" +
                                    e.getMessage()
                    );
                    error.setValue("Пользователь уже существует!");
                });
    }
}