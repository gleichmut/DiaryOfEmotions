package com.fuxkinghatred.diaryofemotions.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.NoteViewModel;

/**
 * Фабрика для создания экземпляра {@link NoteViewModel}.
 */
public class NoteViewModelFactory implements ViewModelProvider.Factory {

    /**
     * Репозиторий для работы с заметками.
     */
    private final NoteRepository noteRepository;

    /**
     * Конструктор фабрики.
     *
     * @param noteRepository репозиторий для работы с заметками.
     */
    public NoteViewModelFactory(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
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
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NoteViewModel.class)) {
            return (T) new NoteViewModel(noteRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}