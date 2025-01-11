package com.fuxkinghatred.diaryofemotions.constants;

/**
 * Класс для хранения различных констант приложения.
 */
public class Constants {

    /**
     * Внутренний класс для хранения тегов для логирования.
     */
    public static class Debug {

        /**
         * Тег для логирования в активности добавления заметки.
         */
        public static final String TAG_ADD_NOTE_ACTIVITY = "AddNoteActivity";

        /**
         * Тег для логирования в активности авторизации.
         */
        public static final String TAG_LOGIN_ACTIVITY = "LoginActivity";

        /**
         * Тег для логирования в активности списка заметок.
         */
        public static final String TAG_MY_NOTES_ACTIVITY = "MyNotesActivity";

        /**
         * Тег для логирования в активности заметки.
         */
        public static final String TAG_NOTE_ACTIVITY = "NoteActivity";

        /**
         * Тег для логирования в активности регистрации.
         */
        public static final String TAG_REGISTRATION_ACTIVITY = "RegisterActivity";

        /**
         * Тег для логирования в репозитории заметок.
         */
        public static final String TAG_NOTE_REPOSITORY = "NoteRepository";

        /**
         * Тег для логирования в ViewModel входа.
         */
        public static final String TAG_LOGIN_VIEW_MODEL = "LoginViewModel";

        /**
         * Тег для логирования в ViewModel регистрации.
         */
        public static final String TAG_REGISTRATION_VIEW_MODEL = "RegistrationViewModel";

        /**
         * Тег для логирования в активности анализа фотографии.
         */
        public static final String TAG_ANALYZE_PHOTO_ACTIVITY = "AnalyzePhotoActivity";

        /**
         * Тег для логирования в ViewModel анализа фотографии.
         */
        public static final String TAG_ANALYZE_PHOTO_VIEW_MODEL = "AnalyzePhotoViewModel";

        /**
         * Тег для логирования в адаптере изображений.
         */
        public static final String TAG_IMAGE_ADAPTER = "ImageAdapter";
        /**
         * Тег для логирования в репозитории предсказания эмоций.
         */
        public static final String TAG_EMOTION_PREDICTION_REPOSITORY = "EmotionPredictionRepository";
    }

    /**
     * Внутренний класс для хранения ключей интентов.
     */
    public static class Extras {

        /**
         * Экстра для передачи идентификатора текущего пользователя.
         */
        public static final String EXTRA_CURRENT_USER = "currentUserId";

        /**
         * Экстра для передачи URI изображения.
         */
        public static final String EXTRA_IMAGE_URI = "imageUri";

        /**
         * Экстра для передачи текущей заметки.
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
         * Код запроса для записи аудио.
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

    public static class Urls {
        public static final String BASE_URL_RETROFIT = "https://fuxkinghatred.pythonanywhere.com";
    }
}