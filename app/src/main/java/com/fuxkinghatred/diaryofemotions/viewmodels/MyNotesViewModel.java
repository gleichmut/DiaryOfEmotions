package com.fuxkinghatred.diaryofemotions.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

/**
 * ViewModel для MyNotesActivity.
 */
public class MyNotesViewModel extends ViewModel {
    /**
     * Экземпляр FirebaseAuth для взаимодействия с Firebase Authentication.
     */
    private final FirebaseAuth auth;

    /**
     * Конструктор класса.
     */
    public MyNotesViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Выполняет выход пользователя из системы.
     */
    public void logout() {
        auth.signOut();
    }
}