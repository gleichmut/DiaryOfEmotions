package com.fuxkinghatred.diaryofemotions.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;


import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;

import java.util.List;

/**
 * ViewModel для NoteActivity.
 */
public class NoteViewModel extends ViewModel {
    /**
     * Репозиторий для работы с заметками.
     */
    private final NoteRepository repository;

    /**
     * Конструктор класса.
     *
     * @param repository репозиторий для работы с заметками.
     */
    public NoteViewModel(NoteRepository repository) {
        this.repository = repository;
    }

    /**
     * Возвращает LiveData списка заметок текущего пользователя.
     * Запрашивает данные через NoteRepository.
     *
     * @return LiveData списка заметок текущего пользователя.
     */
    public LiveData<List<Note>> getAllNotesForCurrentUser() {
        return repository.getAllNotesForCurrentUser();
    }

    /**
     * Удаление заметки.
     *
     * @param noteId ID заметки.
     */
    public void deleteNote(String noteId) {
        repository.deleteNoteFromDatabase(noteId);
    }
}