package com.fuxkinghatred.diaryofemotions.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;

/**
 * ViewModel для экрана добавления/редактирования заметки.
 */
public class AddNoteViewModel extends ViewModel {
    /**
     * Репозиторий для работы с заметками.
     */
    private final NoteRepository repository;

    /**
     * Конструктор ViewModel.
     *
     * @param repository Репозиторий для работы с заметками.
     */
    public AddNoteViewModel(NoteRepository repository) {
        this.repository = repository;
    }

    /**
     * Сохраняет заметку через репозиторий.
     *
     * @param note Заметка для сохранения.
     * @return LiveData с сохраненной заметкой.
     */
    public LiveData<Note> saveNote(Note note) {
        return repository.saveNote(note);
    }
}