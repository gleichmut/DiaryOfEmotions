package com.fuxkinghatred.diaryofemotions.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;

import java.util.List;

/**
 * ViewModel для управления данными заметок.
 */
public class NoteViewModel extends ViewModel {
    /**
     * Репозиторий заметок.
     */
    private final NoteRepository repository;

    /**
     * Конструктор класса.
     *
     * @param repository Репозиторий заметок.
     */
    public NoteViewModel(NoteRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает LiveData со списком всех заметок текущего пользователя.
     * Запрашивает данные через NoteRepository.
     *
     * @return LiveData со списком заметок текущего пользователя.
     */
    public LiveData<List<Note>> getAllNotesForCurrentUser() {
        return repository.getAllNotesForCurrentUser();
    }
}