package com.fuxkinghatred.diaryofemotions.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.EmotionPrediction;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Репозиторий для работы с предсказаниями эмоций в Firebase Realtime Database.
 */
public class EmotionPredictionRepository {
    /**
     * Тег для логирования.
     */
    private static final String                   TAG               = Constants.Debug.TAG_EMOTION_PREDICTION_REPOSITORY;
    /**
     * Ссылка на таблицу предсказаний эмоций в Firebase Realtime Database.
     */
    private final        DatabaseReference        databaseReference = FirebaseDatabase.getInstance()
            .getReference(Constants.DatabaseReferences.TABLE_EMOTION_PREDICTION);
    /**
     * LiveData для отслеживания статуса сохранения предсказания.
     */
    private final        MutableLiveData<Boolean> isSaved           = new MutableLiveData<>(false);

    /**
     * Конструктор репозитория.
     */
    public EmotionPredictionRepository() {
    }

    /**
     * Сохраняет предсказание эмоции в Firebase Realtime Database.
     *
     * @param h       Значение H (Hue) цветовой модели HSL.
     * @param s       Значение S (Saturation) цветовой модели HSL.
     * @param l       Значение L (Lightness) цветовой модели HSL.
     * @param emotion Название эмоции.
     * @return LiveData со статусом сохранения (true - успешно, false - ошибка).
     */
    public LiveData<Boolean> saveEmotionPrediction(int h, int s, int l, String emotion) {
        // Создание объекта предсказания эмоции
        EmotionPrediction prediction = new EmotionPrediction(h, s, l, emotion);
        // Получение ссылки для новой записи в базе данных
        DatabaseReference newPredictionRef = databaseReference.push();
        // Сохранение предсказания в базе данных
        newPredictionRef.setValue(prediction)
                .addOnSuccessListener(aVoid -> {
                    Log.d(
                            TAG,
                            "saveEmotionPrediction: " +
                                    "Успешное сохранение предсказания"
                    );
                    // Установка значения true в LiveData при успешном сохранении
                    isSaved.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(
                            TAG,
                            "saveEmotionPrediction: " +
                                    "Ошибка сохранения предсказания"
                    );
                    // Установка значения false в LiveData при ошибке сохранения
                    isSaved.setValue(false);
                });
        // Возвращение LiveData со статусом сохранения
        return isSaved;
    }
}