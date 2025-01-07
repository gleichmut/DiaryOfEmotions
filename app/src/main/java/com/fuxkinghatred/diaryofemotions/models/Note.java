package com.fuxkinghatred.diaryofemotions.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Класс, представляющий заметку.
 */
public class Note implements Parcelable {

    /**
     * Уникальный идентификатор заметки.
     */
    public String noteId;

    /**
     * Идентификатор пользователя, которому принадлежит эта заметка.
     */
    public String userId;

    /**
     * Временная метка создания заметки.
     */
    public long timestamp;

    /**
     * Заголовок заметки.
     */
    public String title;

    /**
     * Основной текст заметки.
     */
    public String text;

    /**
     * Эмоция, ассоциированная с заметкой.
     */
    public String emotion;

    /**
     * Список фотографий, связанных с заметкой.
     */
    public List<Photo> photos;

    /**
     * Получает уникальный идентификатор заметки.
     *
     * @return уникальный идентификатор заметки
     */
    public String getNoteId() {
        return noteId;
    }

    /**
     * Пустой конструктор, необходимый для работы с Parcelable.
     */
    public Note() {
    }

    /**
     * Генерация уникального идентификатора для записи.
     *
     * @return сгенерированный уникальный идентификатор
     */
    private String generateNoteId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Конструктор для создания новой заметки.
     *
     * @param userId    идентификатор пользователя
     * @param timestamp временная метка создания заметки
     * @param title     заголовок заметки
     * @param text      основной текст заметки
     * @param emotion   эмоция, ассоциированная с заметкой
     * @param photos    список фотографий, связанных с заметкой
     */
    public Note(String userId,
                long timestamp,
                String title,
                String text,
                String emotion,
                List<Photo> photos) {
        this.noteId    = generateNoteId();
        this.userId    = userId;
        this.timestamp = timestamp;
        this.title     = title;
        this.text      = text;
        this.emotion   = emotion;
        this.photos    = new ArrayList<>();

        if (photos != null)
            this.photos.addAll(photos);
    }

    /**
     * Конструктор для восстановления состояния объекта из Parcel.
     *
     * @param in объект Parcel, содержащий данные для восстановления
     */
    protected Note(Parcel in) {
        noteId    = in.readString();
        userId    = in.readString();
        timestamp = in.readLong();
        title     = in.readString();
        text      = in.readString();
        emotion   = in.readString();
        photos    = in.createTypedArrayList(Photo.CREATOR);
    }

    /**
     * Возвращает строковое представление объекта Note.
     *
     * @return строковое представление объекта
     */
    @NonNull
    @Override
    public String toString() {
        return "Note{" +
                "noteId='" + noteId + '\'' +
                ", userId='" + userId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", emotion='" + emotion + '\'' +
                ", photos='" + photos + '\'' +
                ", photos (size)='" + (photos != null ? photos.size() : 0) +
                '}';
    }

    /**
     * Метод, возвращающий содержимое объекта, которое должно быть сохранено.
     *
     * @return всегда возвращает 0, так как нет необходимости в дополнительных данных
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Запись содержимого объекта в Parcel.
     *
     * @param parcel объект Parcel, в который будет записано состояние объекта
     * @param i      флаги, определяющие поведение при записи
     */
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(noteId);
        parcel.writeString(userId);
        parcel.writeLong(timestamp);
        parcel.writeString(title);
        parcel.writeString(text);
        parcel.writeString(emotion);
        parcel.writeTypedList(photos);
    }

    /**
     * Объект Creator, используемый для создания новых экземпляров класса Note из Parcel.
     */
    public static final Creator<Note> CREATOR = new Creator<>() {
        /**
         * Создание нового объекта Note из Parcel.
         *
         * @param in объект Parcel, содержащий данные для восстановления
         * @return восстановленный объект Note
         */
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        /**
         * Создание массива объектов Note заданного размера.
         *
         * @param size размер массива
         * @return массив объектов Note указанного размера
         */
        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}