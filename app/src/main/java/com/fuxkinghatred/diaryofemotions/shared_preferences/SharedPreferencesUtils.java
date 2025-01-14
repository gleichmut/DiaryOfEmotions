package com.fuxkinghatred.diaryofemotions.shared_preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Утилиты для Shared Preferences.
 */
public class SharedPreferencesUtils {

    /**
     * Название файла SharedPreferences.
     */
    private static final String PREFS_NAME = "user_prefs";
    /**
     * Ключ для хранения состояния авторизации.
     */
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    /**
     * Сохраняет состояние авторизации пользователя в SharedPreferences.
     *
     * @param context  Контекст приложения.
     * @param loggedIn Состояние авторизации (true - авторизован, false - не авторизован).
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        // Получение SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Запись состояния авторизации
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    /**
     * Проверяет, авторизован ли пользователь.
     *
     * @param context Контекст приложения.
     * @return true, если пользователь авторизован, false - в противном случае.
     */
    public static boolean isLoggedIn(Context context) {
        // Получение SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Чтение состояния авторизации, по умолчанию false
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}