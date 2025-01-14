package com.fuxkinghatred.diaryofemotions.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.adapters.ImageAdapter;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.models.Photo;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.AddNoteViewModel;
import com.fuxkinghatred.diaryofemotions.viewmodels.DateTimePickerViewModel;
import com.fuxkinghatred.diaryofemotions.factories.AddNoteViewModelFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Activity добавления / редактирования заметки.
 */
public class AddNoteActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_ADD_NOTE_ACTIVITY;

    /**
     * Текстовое поле для отображения выбранной даты.
     */
    private TextView textViewSelectedDate;

    /**
     * Текстовое поле для отображения выбранного времени.
     */
    private TextView textViewSelectedTime;

    /**
     * Текстовое поле для отображения действия (добавление/редактирование).
     */
    private TextView textViewActionNote;

    /**
     * Поле ввода заголовка заметки.
     */
    private EditText editTextTitleOfNote;

    /**
     * Поле ввода текста заметки.
     */
    private EditText editTextTextOfNote;

    /**
     * Кнопка выбора даты.
     */
    private ImageButton imageButtonSelectDate;

    /**
     * Кнопка выбора времени.
     */
    private ImageButton imageButtonSelectTime;
    /**
     * Кнопка загрузки фото.
     */
    private ImageButton imageButtonUploadPhoto;

    /**
     * Кнопка начала распознавания речи.
     */
    private ImageButton imageButtonRecognizeSpeech;

    /**
     * Кнопка очистки текста заметки.
     */
    private ImageButton imageButtonClearTextOfNote;

    /**
     * Кнопка сохранения заметки.
     */
    private Button buttonSave;

    /**
     * RecyclerView для отображения изображений.
     */
    private RecyclerView recyclerViewImageView;

    /**
     * ViewModel для выбора даты и времени.
     */
    private DateTimePickerViewModel dateTimePickerViewModel;

    /**
     * ViewModel для AddNoteActivity.
     */
    private AddNoteViewModel addNoteViewModel;

    /**
     * Адаптер для RecyclerView с изображениями.
     */
    private ImageAdapter imageAdapter;

    /**
     * Лаунчер запроса разрешений.
     */
    private ActivityResultLauncher<String[]> permissionLauncher;

    /**
     * Лаунчер камеры.
     */
    private ActivityResultLauncher<Intent> cameraLauncher;

    /**
     * Лаунчер галереи.
     */
    private ActivityResultLauncher<Intent> galleryLauncher;

    /**
     * Лаунчер распознавания речи.
     */
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;

    /**
     * Список URI изображений.
     */
    private final List<Uri> imageUris = new ArrayList<>();

    /**
     * URI текущего изображения.
     */
    private Uri imageUri;

    /**
     * Заголовок заметки.
     */
    private String title;

    /**
     * Текст заметки.
     */
    private String text;

    /**
     * Эмоция заметки.
     */
    private String emotion;

    /**
     * Загруженная заметка (если передана заметка на редактирование).
     */
    private Note loadedNote;

    /**
     * Handler для периодического обновления времени.
     */
    private final Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollViewAddNoteActivity),
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
        // Установка адаптеров
        setAdapters();
        // Установка ViewModel
        setViewModelProvider();
        // Установка лаунчеров
        setActivityLaunchers();
        // Установка слушателей
        setListeners();
        // Установка наблюдателей
        setObservers();

        // Получаем переданную заметку на редактирование
        loadedNote = getIntent().getParcelableExtra(Constants.Extras.EXTRA_CURRENT_NOTE);
        // Если заметка передана
        if (loadedNote != null) {
            // Устанавливаем название активности
            textViewActionNote.setText("Редактирование заметки");
            // Загружаем данные заметки
            loadNoteData(loadedNote);
            // Устанавливаем дату и время заметки
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(loadedNote.timestamp);
            dateTimePickerViewModel.setDate(calendar);
            dateTimePickerViewModel.setTime(calendar);
        } else {
            // Устанавливаем название активности
            textViewActionNote.setText("Добавление заметки");
        }
        // Установка начальных даты и времени
        setInitialDateTime();
    }

    // Установка начальных даты и времени
    private void setInitialDateTime() {
        Calendar now = Calendar.getInstance();
        dateTimePickerViewModel.setDate(now);
        dateTimePickerViewModel.setTime(now);
        setDate();
        setTime();
    }

    /***
     * Удаление всех отложенных задач при уничтожении Activity.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Загружает данные заметки в элементы управления.
     *
     * @param note заметка для загрузки
     */
    private void loadNoteData(Note note) {
        // Установка заголовка заметки
        editTextTitleOfNote.setText(note.title);
        // Установка текста заметки
        editTextTextOfNote.setText(note.text);

        // Если есть фото
        if (note.photos != null) {
            for (Photo photo : note.photos) {
                // Добавляем URI изображения в список
                imageUris.add(Uri.parse(photo.url));
                Log.d(
                        TAG,
                        "loadNoteData: " +
                                "photo.url: " + Uri.parse(photo.url)
                );
            }
            // Уведомляем адаптер об изменении данных
            imageAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Устанавливает наблюдателей за LiveData.
     */
    private void setObservers() {
        dateTimePickerViewModel.getIsDateTimeValid().observe(this, isValid -> Log.d(
                TAG,
                "setObservers: " +
                        "dateTimePickerViewModel.getIsDateTimeValid().observe is set"
        ));
    }

    /**
     * Проверяет разрешения и запускает распознавание речи.
     */
    private void checkPermissionsAndStartSpeechRecognition() {
        // Если разрешения не даны
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешения
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    Constants.Requests.REQUEST_CODE_RECORD_AUDIO);
        }
        // Если разрешения даны
        else {
            // Запускаем распознавание речи
            startRecognizeSpeech();
        }
    }

    /**
     * Обрабатывает результаты запроса разрешений.
     *
     * @param requestCode  код запроса
     * @param permissions  запрошенные разрешения
     * @param grantResults результаты запроса
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Если дано разрешение на микрофон
        if (requestCode == Constants.Requests.REQUEST_CODE_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Запускаем распознавание речи
                startRecognizeSpeech();
            }
            // Если не дано разрешение на микрофон
            else {
                // Уведомляем пользователя о том, что разрешения не получены
                Toast.makeText(this,
                        R.string.microphone_permission_not_granted,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Устанавливает ActivityResultLauncher для:
     * распознавания речи, запуска разрешений, запуска камеры, запуска галереи.
     */
    private void setActivityLaunchers() {
        // Распознавание речи
        speechRecognizerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            ArrayList<String> matches =
                                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                            if (matches != null && !matches.isEmpty()) {
                                String spokenText = matches.get(0);
                                // Добавляем распознанный текст в поле
                                editTextTextOfNote.append(spokenText);
                            }
                        }
                    }
                });
        // Запрос разрешений
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                isGranted -> {
                    if (isGranted.containsValue(false))
                        // Уведомляем пользователя о том, что разрешения не получены
                        Toast.makeText(this,
                                R.string.permissions_not_granted,
                                Toast.LENGTH_SHORT).show();
                    else
                        // Показываем диалог выбора источника изображения
                        showImageSourceDialog();
                });

        // Запуск камеры
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK)
                        // Добавляем изображение
                        addImage(imageUri);
                });

        // Запуск галереи
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        // Если выбрано изображение
                        if (selectedImageUri != null) {
                            // Копируем изображение из галереи в директорию приложения
                            copyImageToAppDir(selectedImageUri);
                        } else {
                            // Уведомляем пользователя об ошибке
                            Toast.makeText(this,
                                    R.string.error_copy_image,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Копирует изображение из выбранного URI в директорию приложения.
     *
     * @param selectedImageUri URI выбранного изображения
     */
    private void copyImageToAppDir(Uri selectedImageUri) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DISPLAY_NAME,
                "image_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));

        ContentResolver resolver = getContentResolver();
        try {
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // Если изображение не выбрано
            if (imageUri == null) {
                Log.e(
                        TAG,
                        "copyImageToAppDir: " +
                                R.string.error_creating_uri
                );
                // Уведомляем пользователя об ошибке
                Toast.makeText(this,
                        R.string.error_creating_uri,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream inputStream = resolver.openInputStream(selectedImageUri);
            if (inputStream != null) {
                OutputStream outputStream = resolver.openOutputStream(imageUri);
                if (outputStream != null) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                    outputStream.close();
                    // Добавляем изображение
                    addImage(imageUri);
                }
            }

        } catch (IOException e) {
            Log.e(
                    TAG,
                    "copyImageToAppDir: " +
                            R.string.error_copy_image
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.error_copy_image,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Добавляет изображение в список и уведомляет адаптер.
     *
     * @param imageUri URI изображения
     */
    private void addImage(Uri imageUri) {
        // Если изображение не передано
        if (imageUri == null) {
            Log.e(
                    TAG,
                    "addImage: " +
                            R.string.error_loading_image
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.error_loading_image,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Добавляем изображение в список
        imageUris.add(imageUri);
        // Уведомляем адаптер об изменении данных
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * Устанавливает слушателей.
     */
    private void setListeners() {
        // Слушатель кнопки выбора даты
        imageButtonSelectDate.setOnClickListener(view -> onShowDatePicker());
        // Слушатель кнопки выбора времени
        imageButtonSelectTime.setOnClickListener(view -> onShowTimePicker());
        // Слушатель кнопки распознавания речи
        imageButtonRecognizeSpeech.setOnClickListener(view -> checkPermissionsAndStartSpeechRecognition());
        // Слушатель кнопки очистки текста
        imageButtonClearTextOfNote.setOnClickListener(view -> clearTextOfNote());
        // Слушатель кнопки загрузки фото
        imageButtonUploadPhoto.setOnClickListener(view -> checkAndRequestPermissions());
        // Слушатель кнопки сохранения
        buttonSave.setOnClickListener(view -> saveNote());
    }

    /**
     * Очищает текст заметки.
     */
    private void clearTextOfNote() {
        editTextTextOfNote.getText().clear();
    }

    /**
     * Запускает Activity для распознавания речи.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.say_something));

        try {
            // Запуск распознавания речи
            speechRecognizerLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(
                    TAG,
                    "startVoiceRecognitionActivity: " +
                            R.string.error_starting_speech_recognition
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.error_starting_speech_recognition,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Проверяет наличие поддержки распознавания речи.
     */
    private void startRecognizeSpeech() {
        PackageManager packageManager = getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> activities = packageManager
                .queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.isEmpty()) {
            Log.e(
                    TAG,
                    "startRecognizeSpeech: " +
                            R.string.speech_recognition_not_supported
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.speech_recognition_not_supported,
                    Toast.LENGTH_SHORT).show();
        } else {
            // Запуск распознавания речи
            startVoiceRecognitionActivity();
        }
    }

    /**
     * Проверяет заполненность обязательных полей.
     *
     * @return true, если все поля заполнены, иначе false
     */
    private boolean checkFields() {
        boolean isValid = true;
        String errorMessage = "";

        if (TextUtils.isEmpty(textViewSelectedDate.getText())) {
            isValid = false;
            // Сообщение об ошибке выбора даты
            errorMessage = getString(R.string.select_date);
        } else if (TextUtils.isEmpty(textViewSelectedTime.getText())) {
            isValid = false;
            // Сообщение об ошибке выбора времени
            errorMessage = getString(R.string.select_time);
        } else if (TextUtils.isEmpty(editTextTitleOfNote.getText())) {
            isValid = false;
            // Сообщение об ошибке ввода заголовка
            errorMessage = getString(R.string.enter_title);
        } else if (TextUtils.isEmpty(editTextTextOfNote.getText())) {
            isValid = false;
            // Сообщение об ошибке ввода текста
            errorMessage = getString(R.string.enter_text);
        }
        if (!isValid) {
            Log.e(
                    TAG,
                    "checkFields: " +
                            errorMessage
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    errorMessage,
                    Toast.LENGTH_SHORT).show();
        }
        return isValid;
    }

    /**
     * Возвращает текст из EditText без пробелов по краям.
     *
     * @param editText поле ввода
     * @return текст без пробелов
     */
    private String getTrimmedValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * Получает данные заметки из полей ввода.
     */
    private void getNoteData() {
        // Получаем заголовок
        title = getTrimmedValue(editTextTitleOfNote);
        // Получаем текст
        text = getTrimmedValue(editTextTextOfNote);
    }

    /**
     * Сохраняет заметку.
     */
    private void saveNote() {
        // Проверка полей
        if (!checkFields()) return;

        // Проверка валидности даты и времени
        if (Boolean.FALSE.equals(dateTimePickerViewModel.getIsDateTimeValid().getValue())) {
            Log.e(
                    TAG,
                    "saveNote: " +
                            R.string.datetime_is_invalid
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.datetime_is_invalid,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Получение данных заметки
        getNoteData();

        // Создание списка фото
        List<Photo> photos = imageUris.stream()
                .map(uri -> new Photo(uri.toString()))
                .collect(Collectors.toList());
        // Получение времени заметки
        long datetime = dateTimePickerViewModel.getDatetime();

        if (loadedNote == null) {
            // Создание новой заметки
            loadedNote = new Note(getExtraIds(), datetime, title, text, emotion, photos);
        } else {
            // Обновление заголовка
            loadedNote.title = title;
            // Обновление текста
            loadedNote.text = text;
            // Обновление фото
            loadedNote.photos = photos;
            // Обновление времени
            loadedNote.timestamp = datetime;
        }

        // Наблюдатель сохранения заметки
        addNoteViewModel.saveNote(loadedNote).observe(this, savedNote -> {
            Log.d(
                    TAG,
                    "saveNote: " +
                            "addNoteViewModel.saveNote(loadedNote).observe is set"
            );
            // Если заметка заполнена
            if (savedNote != null) {
                Log.d(
                        TAG,
                        "saveNote: " +
                                R.string.note_saved
                );
                // Уведомляем пользователя об успешности выполнения
                Toast.makeText(this,
                        R.string.note_saved,
                        Toast.LENGTH_SHORT).show();
                Intent intent = NoteActivity.newIntent(AddNoteActivity.this, loadedNote);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Log.e(
                        TAG,
                        "saveNote: " +
                                R.string.note_not_saved
                );
                // Уведомляем пользователя об ошибке
                Toast.makeText(this,
                        R.string.note_not_saved,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Инициализирует ViewModel.
     */
    private void setViewModelProvider() {
        // Создание репозитория
        NoteRepository repository = new NoteRepository(getExtraIds());
        // Создание ViewModel даты и времени
        dateTimePickerViewModel = new ViewModelProvider(this)
                .get(DateTimePickerViewModel.class);
        // Создание фабрики для AddNoteViewModel
        AddNoteViewModelFactory factory = new AddNoteViewModelFactory(repository);
        // Создание ViewModel для AddNoteViewModel
        addNoteViewModel = new ViewModelProvider(this, factory).get(AddNoteViewModel.class);
    }

    /**
     * Устанавливает адаптеры.
     */
    private void setAdapters() {
        // Создание адаптера изображений
        imageAdapter = new ImageAdapter(AddNoteActivity.this,
                imageUris,
                this,
                null);
        // Установка менеджера компоновки
        recyclerViewImageView.setLayoutManager(new GridLayoutManager(this, 3));
        // Установка адаптера RecyclerView
        recyclerViewImageView.setAdapter(imageAdapter);
    }

    /**
     * Проверяет и запрашивает необходимые разрешения.
     */
    private void checkAndRequestPermissions() {
        Log.d(
                TAG,
                "checkAndRequestPermissions run "
        );
        // Список необходимых разрешений
        List<String> permissions = List.of(Manifest.permission.CAMERA);
        // Фильтрация разрешений для запроса
        List<String> permissionsToRequest = permissions.stream()
                .filter(permission ->
                        ContextCompat.checkSelfPermission(this, permission)
                                != PackageManager.PERMISSION_GRANTED)
                .collect(Collectors.toList());

        // Если разрешения даны
        if (permissionsToRequest.isEmpty()) {
            // Показываем диалог выбора источника
            showImageSourceDialog();
        } else {
            // Запрашиваем разрешения
            permissionLauncher.launch(permissionsToRequest.toArray(new String[0]));
        }
    }

    /**
     * Показывает диалог выбора источника изображения (камера или галерея).
     */
    private void showImageSourceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_source_image)
                .setItems(new String[]{getString(R.string.camera), getString(R.string.gallery)}, (dialog, which) -> {
                    switch (which) {
                        // Камера
                        case 0:
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,
                                    "image_" + System.currentTimeMillis() + ".jpg");
                            values.put(MediaStore.Images.Media.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name));

                            ContentResolver resolver = getContentResolver();
                            try {
                                // Создание URI изображения
                                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        values);
                                if (imageUri == null) {
                                    Log.e(
                                            TAG,
                                            "showImageSourceDialog: " +
                                                    R.string.error_creating_uri
                                    );
                                    // Уведомляем пользователя об ошибке
                                    Toast.makeText(this,
                                            R.string.error_creating_uri,
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                cameraLauncher.launch(cameraIntent);
                            } catch (Exception e) {
                                Log.e(
                                        TAG,
                                        "showImageSourceDialog: " +
                                                R.string.error_creating_uri
                                );
                                // Уведомляем пользователя об ошибке
                                Toast.makeText(this,
                                        R.string.error_creating_uri,
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            break;
                        // Галерея
                        case 1:
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryLauncher.launch(galleryIntent);
                            break;
                    }
                })
                .show();
    }

    /**
     * Показывает диалог выбора даты.
     */
    public void onShowDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearDpd, monthDpd, dayDpd) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(Calendar.YEAR, yearDpd);
                    selectedDate.set(Calendar.MONTH, monthDpd);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayDpd);
                    // Установка выбранной даты
                    dateTimePickerViewModel.setDate(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Показывает диалог выбора времени.
     */
    public void onShowTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourTpd, minuteTpd) -> {
                    Calendar selectedTime = Calendar.getInstance();
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourTpd);
                    selectedTime.set(Calendar.MINUTE, minuteTpd);
                    // Установка выбранного времени
                    dateTimePickerViewModel.setTime(selectedTime);
                }, hour, minutes, true);
        timePickerDialog.show();
    }

    /**
     * Устанавливает дату в TextView и обновляет ViewModel.
     */
    private void setDate() {
        dateTimePickerViewModel.getSelectedDate().observe(this, calendar -> {
            Log.d(
                    TAG,
                    "setDate: " +
                            "dateTimePickerViewModel.getSelectedDate().observe is set"
            );
            if (calendar != null) {
                // Отображаем дату
                textViewSelectedDate.setText(formatDate(calendar));
                // Обновляем время
                checkAndUpdateDatetime();
            }
        });
    }

    /**
     * Устанавливает время в TextView и обновляет ViewModel.
     */
    private void setTime() {
        dateTimePickerViewModel.getSelectedTime().observe(this, calendar -> {
            Log.d(
                    TAG,
                    "setTime: " +
                            "dateTimePickerViewModel.getSelectedTime().observe is set"
            );
            if (calendar != null) {
                // Отображаем время
                textViewSelectedTime.setText(formatTime(calendar));
                // Обновляем время и запускаем периодическую проверку
                checkAndUpdateDatetime();
                startRealTimeValidation();
            }
        });
    }

    /**
     * Запускает проверку времени раз в секунду.
     */
    private void startRealTimeValidation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAndUpdateDatetime();
                // Вызов handler через каждую секунду
                handler.postDelayed(this, 1000);
            }
        }, 1000); // Запуск через 1 секунду
    }

    /**
     * Проверяет и обновляет дату и время в ViewModel.
     */
    private void checkAndUpdateDatetime() {
        if (dateTimePickerViewModel.getSelectedDate().getValue() != null &&
                dateTimePickerViewModel.getSelectedTime().getValue() != null) {
            // Обновление времени в ViewModel
            dateTimePickerViewModel.updateDatetime(Calendar.getInstance());
        }
    }

    /**
     * Форматирует дату в строку.
     *
     * @param calendar календарь
     * @return отформатированная строка
     */
    private String formatDate(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.d_mmmm_yyyy),
                new Locale(getString(R.string.ru)));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Форматирует время в строку.
     *
     * @param calendar календарь
     * @return отформатированная строка
     */
    private String formatTime(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.hh_mm),
                new Locale(getString(R.string.ru)));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Обрабатывает нажатие на элемент списка изображений.
     *
     * @param imageUri URI изображения
     */
    @Override
    public void onItemClick(Uri imageUri) {
        Intent intent = new Intent(this, EnlargedImageViewActivity.class);
        // Передаем URI изображения в активность
        intent.putExtra(Constants.Extras.EXTRA_IMAGE_URI, imageUri.toString());
        startActivity(intent);
    }

    /**
     * Обрабатывает долгое нажатие на элемент списка изображений.
     *
     * @param imageUri URI изображения
     * @param position позиция элемента в списке
     */
    @Override
    public void onItemLongClick(Uri imageUri, int position) {
        // Удаляем URI из списка
        imageUris.remove(position);
        // Уведомляем адаптер об удалении
        imageAdapter.notifyItemRemoved(position);
    }

    /**
     * Получает id пользователя или заметки из Intent.
     *
     * @return id пользователя или заметки
     */
    private String getExtraIds() {
        if (getIntent().getStringExtra(Constants.Extras.EXTRA_CURRENT_USER) != null) {
            return getIntent().getStringExtra(Constants.Extras.EXTRA_CURRENT_USER);
        } else {
            return getIntent().getStringExtra(Constants.Extras.EXTRA_CURRENT_NOTE);
        }
    }

    /**
     * Инициализирует View элементы.
     */
    private void initViews() {
        textViewSelectedDate       = findViewById(R.id.textViewSelectedDate);
        textViewSelectedTime       = findViewById(R.id.textViewSelectedTime);
        textViewActionNote         = findViewById(R.id.textViewActionNote);
        editTextTitleOfNote        = findViewById(R.id.editTextTitleOfNote);
        editTextTextOfNote         = findViewById(R.id.editTextTextOfNote);
        imageButtonSelectDate      = findViewById(R.id.imageButtonSelectDate);
        imageButtonSelectTime      = findViewById(R.id.imageButtonSelectTime);
        imageButtonUploadPhoto     = findViewById(R.id.imageButtonUploadPhoto);
        imageButtonRecognizeSpeech = findViewById(R.id.imageButtonRecognizeSpeech);
        imageButtonClearTextOfNote = findViewById(R.id.imageButtonClearTextOfNote);
        buttonSave                 = findViewById(R.id.buttonSave);
        recyclerViewImageView      = findViewById(R.id.recyclerViewImageView);
    }

    /**
     * Создает Intent для запуска AddNoteActivity для добавления новой заметки.
     *
     * @param context       контекст
     * @param currentUserId id текущего пользователя
     * @return Intent
     */
    public static Intent newIntent(Context context, String currentUserId) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        intent.putExtra(Constants.Extras.EXTRA_CURRENT_USER, currentUserId);
        return intent;
    }

    /**
     * Создает Intent для запуска AddNoteActivity для редактирования заметки.
     *
     * @param context контекст
     * @param note    заметка для редактирования
     * @return Intent
     */
    public static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        intent.putExtra(Constants.Extras.EXTRA_CURRENT_NOTE, note);
        return intent;
    }
}
