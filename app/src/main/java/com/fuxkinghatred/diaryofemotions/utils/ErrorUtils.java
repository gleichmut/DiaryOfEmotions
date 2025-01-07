package com.fuxkinghatred.diaryofemotions.utils;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * Класс локализации ошибок
 */
public class ErrorUtils {
    /**
     * Возвращает ошибку на русском языке.
     *
     * @param errorCode код ошибки
     * @return ошибка на русском языке
     */
    public static String getLocalizedErrorMessage(@NonNull String errorCode) {

        switch (errorCode) {
            case "ERROR_INVALID_EMAIL":
                return "Неверный формат электронной почты";
            case "ERROR_EMAIL_ALREADY_IN_USE":
                return "Эта электронная почта уже используется";
            case "ERROR_WEAK_PASSWORD":
                return "Слишком слабый пароль";
            case "ERROR_USER_NOT_FOUND":
                return "Пользователь с такой электронной почтой не найден";
            case "ERROR_WRONG_PASSWORD":
                return "Неверный пароль";
            case "ERROR_USER_DISABLED":
                return "Этот аккаунт отключен";
            case "ERROR_TOO_MANY_REQUESTS":
                return "Слишком много попыток. Попробуйте позже";
            case "ERROR_NETWORK_REQUEST_FAILED":
                return "Ошибка сети. Проверьте ваше подключение к интернету";
            case "ERROR_MISSING_EMAIL":
                return "Электронная почта не указана";
            case "ERROR_MISSING_PASSWORD":
                return "Пароль не указан";
            case "ERROR_INVALID_CREDENTIAL":
                return "Неверные учетные данные";
            case "Пользователь уже существует!":
                return "Пользователь уже существует!";
            default:
                Log.d("ErrorUtils", "getLocalizedErrorMessage: " + errorCode);
                return "Произошла ошибка";
        }
    }
}