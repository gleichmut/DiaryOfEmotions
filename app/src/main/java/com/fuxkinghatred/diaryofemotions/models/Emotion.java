package com.fuxkinghatred.diaryofemotions.models;

/**
 * Модель эмоции.
 */
public class Emotion {

    /**
     * Название эмоции.
     */
    public final String emotionName;

    /**
     * Идентификатор ресурса, связанный с этой эмоцией.
     */
    public final int resourceId;

    /**
     * Конструктор создания новой эмоции.
     *
     * @param emotionName название эмоции.
     * @param resourceId  идентификатор ресурса, связанного с эмоцией.
     */
    public Emotion(String emotionName, int resourceId) {
        this.emotionName = emotionName;
        this.resourceId  = resourceId;
    }
}