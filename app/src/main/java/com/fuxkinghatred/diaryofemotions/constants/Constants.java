package com.fuxkinghatred.diaryofemotions.constants;

/**
 * Класс для хранения различных констант приложения.
 */
public class Constants {
    /**
     * Внутренний класс для хранения тегов логирования.
     */
    public static class Debug {

        /**
         * Тег логирования AddNoteActivity.
         */
        public static final String TAG_ADD_NOTE_ACTIVITY = "AddNoteActivity";

        /**
         * Тег логирования LoginActivity.
         */
        public static final String TAG_LOGIN_ACTIVITY = "LoginActivity";

        /**
         * Тег логирования MyNotesActivity.
         */
        public static final String TAG_MY_NOTES_ACTIVITY = "MyNotesActivity";

        /**
         * Тег логирования NoteActivity.
         */
        public static final String TAG_NOTE_ACTIVITY = "NoteActivity";

        /**
         * Тег логирования RegistrationActivity.
         */
        public static final String TAG_REGISTRATION_ACTIVITY = "RegisterActivity";

        /**
         * Тег логирования NoteRepository.
         */
        public static final String TAG_NOTE_REPOSITORY = "NoteRepository";

        /**
         * Тег логирования LoginViewModel.
         */
        public static final String TAG_LOGIN_VIEW_MODEL = "LoginViewModel";

        /**
         * Тег логирования RegistrationViewModel.
         */
        public static final String TAG_REGISTRATION_VIEW_MODEL = "RegistrationViewModel";

        /**
         * Тег логирования AnalyzePhotosActivity.
         */
        public static final String TAG_ANALYZE_PHOTO_ACTIVITY = "AnalyzePhotoActivity";

        /**
         * Тег логирования AnalyzePhotoViewModel.
         */
        public static final String TAG_ANALYZE_PHOTO_VIEW_MODEL = "AnalyzePhotoViewModel";

        /**
         * Тег логирования ImageAdapter.
         */
        public static final String TAG_IMAGE_ADAPTER = "ImageAdapter";
        /**
         * Тег логирования EmotionPredictionRepository.
         */
        public static final String TAG_EMOTION_PREDICTION_REPOSITORY = "EmotionPredictionRepository";
        /**
         * Тег логирования SwipeToDeleteCallback.
         */
        public static final String TAG_SWIPE_TO_DELETE_CALLBACK = "SwipeToDeleteCallback";
    }

    /**
     * Внутренний класс для хранения ключей Intent.
     */
    public static class Extras {
        /**
         * Экстра передачи идентификатора текущего пользователя.
         */
        public static final String EXTRA_CURRENT_USER = "currentUserId";

        /**
         * Экстра передачи URI изображения.
         */
        public static final String EXTRA_IMAGE_URI = "imageUri";

        /**
         * Экстра передачи текущей заметки.
         */
        public static final String EXTRA_CURRENT_NOTE = "currentNote";
    }

    /**
     * Внутренний класс для хранения запросов к базе данных.
     */
    public static class Queries {
        /**
         * Запрос для сортировки по ID пользователя.
         */
        public static final String QUERY_ORDER_BY_CHILD_USER_ID = "userId";
    }

    /**
     * Внутренний класс для хранения кодов запросов.
     */
    public static class Requests {
        /**
         * Код запроса записи аудио.
         */
        public static final int REQUEST_CODE_RECORD_AUDIO = 1;
    }

    /**
     * Внутренний класс для хранения названий таблиц в базе данных.
     */
    public static class DatabaseReferences {
        /**
         * Таблица с предсказанными эмоциями и значениями цвета.
         */
        public static final String TABLE_EMOTION_PREDICTION = "emotion_prediction";
        /**
         * Таблица с заметками.
         */
        public static final String TABLE_NOTES = "notes";
    }

    /**
     * Внутренний класс для хранения URL.
     */
    public static class Urls {
        /**
         * Ссылка на сервер.
         */
        public static final String BASE_URL_RETROFIT = "https://fuxkinghatred.pythonanywhere.com";
    }
}