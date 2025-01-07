package com.fuxkinghatred.diaryofemotions.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.Application;

import com.fuxkinghatred.diaryofemotions.repositories.EmotionPredictionRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.AnalyzePhotoViewModel;

/**
 * Фабрика для создания экземпляров {@link AnalyzePhotoViewModel}.
 */
public class AnalyzePhotoViewModelFactory implements ViewModelProvider.Factory {
    /**
     * Репозиторий для предсказания эмоций.
     */
    private final EmotionPredictionRepository emotionPredictionRepository;
    /**
     * Экземпляр приложения.
     */
    private final Application                 application;

    /**
     * Конструктор фабрики.
     *
     * @param application                 экземпляр приложения
     * @param emotionPredictionRepository репозиторий для предсказания эмоций
     */
    public AnalyzePhotoViewModelFactory(Application application,
                                        EmotionPredictionRepository emotionPredictionRepository) {
        this.application                 = application;
        this.emotionPredictionRepository = emotionPredictionRepository;
    }

    /**
     * Метод для создания экземпляра ViewModel.
     *
     * @param modelClass класс модели, экземпляр которой требуется создать
     * @return созданный экземпляр модели
     */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AnalyzePhotoViewModel.class)) {
            return (T) new AnalyzePhotoViewModel(application, emotionPredictionRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}