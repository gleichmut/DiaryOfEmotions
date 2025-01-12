
package com.fuxkinghatred.diaryofemotions.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.callbacks.SwipeToDeleteCallback;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.shared_preferences.SharedPreferencesUtils;
import com.fuxkinghatred.diaryofemotions.adapters.MyNotesAdapter;
import com.fuxkinghatred.diaryofemotions.factories.NoteViewModelFactory;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.MyNotesViewModel;
import com.fuxkinghatred.diaryofemotions.viewmodels.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * Активность списка заметок пользователя.
 */
public class MyNotesActivity extends AppCompatActivity implements MyNotesAdapter.OnItemClickListener,
        MyNotesAdapter.OnNoteDeletedListener {
    /**
     * Тег для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_MY_NOTES_ACTIVITY;

    /**
     * ViewModel для работы с заметкой.
     */
    private NoteViewModel noteViewModel;

    /**
     * ViewModel для работы с общими данными о заметках.
     */
    private MyNotesViewModel myNotesViewModel;

    /**
     * Адаптер для RecyclerView.
     */
    private MyNotesAdapter myNotesAdapter;

    /**
     * Индикатор загрузки.
     */
    private ProgressBar progressBar;

    /**
     * Кнопка для выхода из аккаунта.
     */
    private ImageButton imageButtonLogoff;

    /**
     * RecyclerView для отображения списка заметок.
     */
    private RecyclerView recyclerViewMyNotes;

    /**
     * Кнопка для добавления новой заметки.
     */
    private FloatingActionButton buttonAddNote;

    /**
     * Время начала загрузки данных.
     */
    private long startTime;

    /**
     * Лаунчер для добавления заметки.
     */
    private ActivityResultLauncher<Intent> addNoteLauncher;

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.myNotesCoordinatorLayout),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        // Проверка, авторизован ли пользователь
        // Если нет, перенаправляем на экран логина
        if (!SharedPreferencesUtils.isLoggedIn(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // Инициализация view элементов
        initViews();
        progressBar.setVisibility(View.VISIBLE);
        // Установка ViewModelProviders
        setViewModelProviders();
        // Установка наблюдателей
        setObservers();
        // Установка слушателей
        setListeners();
        // Установка лаунчеров
        setActivityLaunchers();
    }

    /**
     * Устанавливает ActivityResultLauncher для добавления заметки.
     */
    private void setActivityLaunchers() {
        addNoteLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        reloadNotes();
                    }
                }
        );
    }

    /**
     * Устанавливает слушателей для UI элементов.
     */
    private void setListeners() {
        // Слушатель для кнопки добавления заметки
        buttonAddNote.setOnClickListener(view -> {
            Log.d(
                    TAG,
                    "setListeners: " +
                            "getExtraIds: " + getExtraIds()
            );
            Intent intent = AddNoteActivity.newIntent(MyNotesActivity.this, getExtraIds());
            addNoteLauncher.launch(intent);
        });

        // Слушатель для кнопки выхода из аккаунта
        imageButtonLogoff.setOnClickListener(view ->
                {
                    myNotesViewModel.logout();
                    Intent intent = LoginActivity.newIntent(MyNotesActivity.this);
                    startActivity(intent);
                    finish();
                }
        );
    }

    /**
     * Устанавливает наблюдателей для LiveData.
     */
    private void setObservers() {
        // Запись времени начала загрузки
        startTime = System.currentTimeMillis();
        // Наблюдение за изменениями списка заметок текущего пользователя
        noteViewModel.getAllNotesForCurrentUser().observe(this, notes -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "noteViewModel.getAllNotesForCurrentUser().observe is set"
            );
            updateRecyclerView(notes);
        });
    }

    /**
     * Метод для запроса LiveData списка заметок
     */
    public void reloadNotes() {
        noteViewModel.getAllNotesForCurrentUser().observe(this, this::updateRecyclerView);
    }

    private void updateRecyclerView(List<Note> notes) {
        if (notes != null) {
            // Запись времени конца загрузки
            long endTime = System.currentTimeMillis();
            // Вычисление времени загрузки
            long loadingTime = endTime - startTime;
            Log.d(
                    TAG,
                    "setObservers: " +
                            "Notes loaded in " + loadingTime + " milliseconds"
            );

            if (myNotesAdapter == null) {
                myNotesAdapter = new MyNotesAdapter(notes, this, this);
                recyclerViewMyNotes.setLayoutManager(new LinearLayoutManager(this));
                recyclerViewMyNotes.setAdapter(myNotesAdapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                        new SwipeToDeleteCallback(myNotesAdapter));
                itemTouchHelper.attachToRecyclerView(recyclerViewMyNotes);
            } else {
                myNotesAdapter.updateData(notes);
            }
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Инициализирует ViewModel.
     */
    private void setViewModelProviders() {
        // Инициализация MyNotesViewModel
        myNotesViewModel = new ViewModelProvider(this).get(MyNotesViewModel.class);
        // Инициализация репозитория заметок с ID текущего пользователя
        NoteRepository repository = new NoteRepository(getExtraIds());
        // Создание фабрики для NoteViewModel
        NoteViewModelFactory factory = new NoteViewModelFactory(repository);
        // Инициализация NoteViewModel
        noteViewModel = new ViewModelProvider(this, factory).get(NoteViewModel.class);
    }

    /**
     * Получает ID текущего пользователя из Intent
     *
     * @return ID текущего пользователя
     */
    private String getExtraIds() {
        return getIntent().getStringExtra(Constants.Extras.EXTRA_CURRENT_USER);
    }

    /**
     * Метод, вызываемый при удалении заметки
     *
     * @param noteId ID удаленной заметки
     * @param note   Удаленная заметка
     */
    @Override
    public void onNoteDeleted(String noteId, Note note) {
        Log.d(TAG, "onNoteDeleted: noteId = " + noteId + ", note text = " + note.text);
        noteViewModel.deleteNote(noteId);
        reloadNotes();
    }

    /**
     * Метод, вызываемый при нажатии на заметку
     *
     * @param note Нажатая заметка
     */
    @Override
    public void onItemClick(Note note) {
        // Создание и запуск intent для перехода к activity просмотра заметки
        Intent intent = NoteActivity.newIntent(MyNotesActivity.this, note);
        startActivity(intent);
    }

    /**
     * Инициализирует View элементы
     */
    private void initViews() {
        imageButtonLogoff   = findViewById(R.id.imageButtonLogoff);
        recyclerViewMyNotes = findViewById(R.id.recyclerViewMyNotes);
        buttonAddNote       = findViewById(R.id.buttonAddNote);
        progressBar         = findViewById(R.id.progressBar);
    }

    /**
     * Создает Intent для запуска MyNotesActivity
     *
     * @param context       Контекст
     * @param currentUserId ID текущего пользователя
     * @return Intent для запуска MyNotesActivity
     */
    public static Intent newIntent(Context context, String currentUserId) {
        Intent intent = new Intent(context, MyNotesActivity.class);
        intent.putExtra(Constants.Extras.EXTRA_CURRENT_USER, currentUserId);
        return intent;
    }
}
