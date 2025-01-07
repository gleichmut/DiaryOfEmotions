package com.fuxkinghatred.diaryofemotions.models;

/**
 * Класс, представляющий прогнозируемую эмоцию.
 */
public class EmotionPrediction {

    /**
     * Значение оттенка (Hue).
     */
    public final int h;

    /**
     * Значение насыщенности (Saturation).
     */
    public final int s;

    /**
     * Значение яркости (Lightness).
     */
    public final int l;

    /**
     * Прогнозируемое имя эмоции.
     */
    public final String emotion;

    /**
     * Конструктор для создания нового прогноза эмоции.
     *
     * @param h       значение оттенка (Hue)
     * @param s       значение насыщенности (Saturation)
     * @param l       значение яркости (Lightness)
     * @param emotion прогнозируемое имя эмоции
     */
    public EmotionPrediction(int h, int s, int l, String emotion) {
        this.h       = h;
        this.s       = s;
        this.l       = l;
        this.emotion = emotion;
    }
}