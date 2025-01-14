package com.fuxkinghatred.diaryofemotions.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.Note;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с заметками в Firebase Realtime Database.
 */
public class NoteRepository {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_NOTE_REPOSITORY;
    /**
     * Ссылка на таблицу заметок в Firebase Realtime Database.
     */
    private final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
            .getReference(Constants.DatabaseReferences.TABLE_NOTES);
    /**
     * ID текущего пользователя.
     */
    private final String currentUserId;

    /**
     * Конструктор репозитория.
     *
     * @param userId ID текущего пользователя.
     */
    public NoteRepository(String userId) {
        this.currentUserId = userId;
    }

    /**
     * Сохраняет заметку.
     *
     * @param note заметка для сохранения.
     * @return LiveData сохраненной заметки.
     */
    public LiveData<Note> saveNote(Note note) {
        MutableLiveData<Note> savedNote = new MutableLiveData<>();
        if (note != null) {
            databaseReference.child(note.getNoteId()).setValue(note)
                    .addOnSuccessListener(success -> {
                        Log.d(
                                TAG,
                                "saveNote: " +
                                        success + " Заметка сохранена!"
                        );
                        savedNote.postValue(note);
                    })
                    .addOnFailureListener(fail -> {
                        Log.d(
                                TAG,
                                "saveNote: " +
                                        fail + " Заметка не сохранена!"
                        );
                        savedNote.postValue(null);
                    });
        }
        return savedNote;
    }

    /**
     * Получает все заметки текущего пользователя.
     *
     * @return LiveData списка заметок.
     */
    public LiveData<List<Note>> getAllNotesForCurrentUser() {
        // Запись времени начала запроса
        long startTime = System.nanoTime();
        MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
        // Создание запроса получения заметок текущего пользователя
        Query query = databaseReference
                .orderByChild(Constants.Queries.QUERY_ORDER_BY_CHILD_USER_ID)
                .equalTo(currentUserId);
        // Слушатель получения данных из базы данных
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Note> notes = new ArrayList<>();
                // Перебор всех дочерних элементов DataSnapshot
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    // Получение объекта Note из DataSnapshot
                    Note note = noteSnapshot.getValue(Note.class);
                    if (note != null)
                        notes.add(note);
                }
                // Сортировка списка заметок по времени (от новых к старым)
                notes.sort((note1, note2) -> Long.compare(note2.timestamp, note1.timestamp));
                // Установка списка заметок в LiveData
                notesLiveData.setValue(notes);
                // Запись времени окончания запроса
                long endTime = System.nanoTime();
                // Вычисление времени выполнения запроса
                long elapsedTime = endTime - startTime;
                // Логирование времени выполнения запроса
                Log.d(
                        TAG,
                        "getAllNotesForCurrentUser: " +
                                "Firebase query time: " + elapsedTime + " ns ("
                                + (elapsedTime / 1000000.0) + " ms)"
                );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Установка null в LiveData при ошибке
                notesLiveData.setValue(null);
                Log.e(
                        TAG,
                        "getAllNotesForCurrentUser: " +
                                "Ошибка получения заметок" + databaseError.getMessage()
                );
            }
        };
        // Добавление слушателя запроса
        query.addValueEventListener(listener);
        // Возврат LiveData списка заметок
        return notesLiveData;
    }

    /**
     * Удаляет заметку.
     *
     * @param noteId ID заметки для удаления.
     * @return Task<Void> для отслеживания статуса удаления.
     */
    public Task<Void> deleteNoteFromDatabase(String noteId) {
        Log.d(
                TAG,
                "deleteNoteFromDatabase: " +
                        "query " + databaseReference.child(noteId)
        );
        return databaseReference.child(noteId).removeValue();
    }
}
