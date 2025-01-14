package com.fuxkinghatred.diaryofemotions.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Модель фото.
 */
public class Photo implements Parcelable {
    /**
     * URL фото.
     */
    public String url;

    /**
     * Пустой конструктор, необходимый для работы с Parcelable.
     */
    public Photo() {
    }

    /**
     * Конструктор создания URL фото.
     *
     * @param url URL фото.
     */
    public Photo(String url) {
        this.url = url;
    }

    /**
     * Конструктор восстановления состояния объекта из Parcel.
     *
     * @param in объект Parcel, содержащий данные для восстановления.
     */
    protected Photo(Parcel in) {
        url = in.readString();
    }

    /**
     * Метод, возвращающий содержимое объекта, которое должно быть сохранено.
     *
     * @return всегда возвращает 0, так как нет необходимости в дополнительных данных.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Запись содержимого объекта в Parcel.
     *
     * @param parcel объект Parcel, в который будет записано состояние объекта.
     * @param i      флаги, определяющие поведение при записи.
     */
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(url);
    }

    /**
     * Объект Creator, используемый для создания новых экземпляров класса Photo из Parcel.
     */
    public static final Creator<Photo> CREATOR = new Creator<>() {
        /**
         * Создание нового объекта Photo из Parcel.
         *
         * @param in объект Parcel, содержащий данные для восстановления.
         * @return восстановленный объект Photo.
         */
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        /**
         * Создание массива объектов Photo заданного размера.
         *
         * @param size размер массива.
         * @return массив объектов Photo указанного размера.
         */
        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}