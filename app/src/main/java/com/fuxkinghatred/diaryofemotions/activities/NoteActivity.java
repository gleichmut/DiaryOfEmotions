
package com.fuxkinghatred.diaryofemotions.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.adapters.ImageAdapter;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.models.Photo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Активность заметки.
 */
public class NoteActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    /**
     * Тег для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_NOTE_ACTIVITY;

    /**
     * TextView для отображения даты и времени заметки.
     */
    private TextView datetimeNoteTextView;

    /**
     * TextView для отображения заголовка заметки.
     */
    private TextView titleNoteTextView;

    /**
     * TextView для отображения текста заметки.
     */
    private TextView textViewTextNote;

    /**
     * TextView для отображения подписи "Фото".
     */
    private TextView textViewPhoto;

    /**
     * Кнопка для закрытия заметки.
     */
    private Button buttonClose;

    /**
     * Кнопка для редактирования заметки.
     */
    private ImageButton imageButtonEdit;

    /**
     * RecyclerView для отображения фотографий заметки.
     */
    private RecyclerView recyclerViewImageView;

    /**
     * TextView для отображения подсказки для начала анализа.
     */
    private TextView textViewClickToAnalyzePhoto;

    /**
     * Объект заметки, которая отображается.
     */
    private Note note;

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollViewNote),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });
        // Инициализация view элементов
        initViews();
        // Загрузка и установка данных заметки
        setSavedNote();
        // Установка слушателей
        setListeners();
    }

    /**
     * Устанавливает слушателей для UI элементов.
     */
    private void setListeners() {
        // Слушатель для кнопки закрытия заметки
        buttonClose.setOnClickListener(view -> finish());
        // Слушатель для кнопки редактирования заметки
        imageButtonEdit.setOnClickListener(view -> editNoteClick());
    }

    /**
     * Обрабатывает нажатие на кнопку редактирования заметки.
     */
    private void editNoteClick() {
        Intent intent = AddNoteActivity.newIntent(NoteActivity.this, note);
        startActivity(intent);
        finish();
    }

    /**
     * Форматирует timestamp в строку даты и времени.
     *
     * @param timestamp timestamp в миллисекундах
     * @return Отформатированная строка даты и времени
     */
    private String formatTimestampToString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.dd_mmmm_yyyy_hh_mm),
                new Locale(getString(R.string.ru)));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Устанавливает данные заметки в UI элементы.
     */
    private void setSavedNote() {
        // Получение объекта заметки из Intent
        note = getIntent().getParcelableExtra(Constants.Extras.EXTRA_CURRENT_NOTE);
        if (note != null) {
            // Установка даты и времени
            datetimeNoteTextView.setText(formatTimestampToString(note.timestamp));
            // Установка заголовка
            titleNoteTextView.setText(note.title);
            // Установка текста заметки
            textViewTextNote.setText(note.text);
            // Получение URI фотографий из строки
            List<Uri> imageUris = getUriFromString(note.photos);
            // Создание и установка адаптера для RecyclerView с фотографиями
            ImageAdapter imageAdapter = new ImageAdapter(this,
                    imageUris,
                    this,
                    textViewClickToAnalyzePhoto);
            recyclerViewImageView.setAdapter(imageAdapter);
            recyclerViewImageView.setLayoutManager(new GridLayoutManager(this, 3));

            if (imageAdapter.getItemCount() > 0) {
                // В адаптере есть элементы для отображения
                Log.d(
                        TAG,
                        "setSavedNote: " +
                                "Есть изображения в RecyclerView"
                );

                // Показываем RecyclerView
                recyclerViewImageView.setVisibility(View.VISIBLE);
            } else {
                // Адаптер пуст, нет элементов для отображения
                Log.d(
                        TAG,
                        "setSavedNote: " +
                                "Нет изображений в RecyclerView"
                );
                // Скрываем RecyclerView, если нет изображений
                recyclerViewImageView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Преобразует список объектов Photo в список URI.
     *
     * @param photos Список объектов Photo
     * @return Список URI фотографий
     */
    private List<Uri> getUriFromString(List<Photo> photos) {
        List<Uri> imageUris = new ArrayList<>();
        if (photos != null) {
            for (Photo photo : photos) {
                Uri uri;
                uri = Uri.parse(photo.url);
                imageUris.add(uri);
            }
            // Показываем надпись "Фото" и информацию для начала анализа
            textViewPhoto.setVisibility(View.VISIBLE);
            textViewClickToAnalyzePhoto.setVisibility(View.VISIBLE);
        } else {
            // Скрываем надпись "Фото" и информацию для начала анализа
            textViewPhoto.setVisibility(View.GONE);
            textViewClickToAnalyzePhoto.setVisibility(View.GONE);
            Log.d(
                    TAG,
                    "getUriFromString: " +
                            "Список photo is NULL, нет изображений для показа"
            );
        }
        return imageUris;
    }

    /**
     * Обрабатывает нажатие на элемент в RecyclerView с фотографиями.
     *
     * @param imageUri URI нажатой фотографии
     */
    @Override
    public void onItemClick(Uri imageUri) {
        Intent intent = AnalyzePhotosActivity.newIntent(NoteActivity.this,
                note,
                imageUri.toString());
        startActivity(intent);
    }

    /**
     * Инициализирует View элементы
     */
    private void initViews() {
        datetimeNoteTextView        = findViewById(R.id.datetimeNoteTextView);
        titleNoteTextView           = findViewById(R.id.titleNoteTextView);
        textViewTextNote            = findViewById(R.id.textViewTextNote);
        textViewPhoto               = findViewById(R.id.textViewPhoto);
        buttonClose                 = findViewById(R.id.buttonClose);
        recyclerViewImageView       = findViewById(R.id.recyclerViewImageView);
        textViewClickToAnalyzePhoto = findViewById(R.id.textViewClickToAnalyzePhoto);
        imageButtonEdit             = findViewById(R.id.imageButtonEdit);
    }

    /**
     * Метод не используется, но является обязательным для интерфейса.
     */
    @Override
    public void onItemLongClick(Uri imageUri, int position) {
    }

    /**
     * Создает Intent для запуска NoteActivity.
     *
     * @param context Контекст
     * @param note    Объект заметки для отображения
     * @return Intent
     */
    public static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.putExtra(Constants.Extras.EXTRA_CURRENT_NOTE, note);
        return intent;
    }
}
