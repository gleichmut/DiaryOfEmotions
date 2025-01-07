package com.fuxkinghatred.diaryofemotions.factories;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.AddNoteViewModel;

/**
 * Фабрика для создания экземпляров {@link AddNoteViewModel}
 */
public class AddNoteViewModelFactory implements ViewModelProvider.Factory {

    private final NoteRepository noteRepository;

    /**
     * Конструктор фабрики, принимающий репозиторий заметок.
     *
     * @param noteRepository репозиторий для работы с заметками
     */
    public AddNoteViewModelFactory(NoteRepository noteRepository) {
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
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddNoteViewModel.class)) {
            return (T) new AddNoteViewModel(noteRepository);
        }
        throw new IllegalArgumentException("Неизвестный класс ViewModel.");
    }
}