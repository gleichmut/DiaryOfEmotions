package com.fuxkinghatred.diaryofemotions.models;

/**
 * Модель предсказания эмоции.
 */
public class EmotionPrediction {
    /**
     * Hue (оттенок).
     */
    public final int h;

    /**
     * Saturation (насыщенность).
     */
    public final int s;

    /**
     * Lightness (яркость).
     */
    public final int l;

    /**
     * Предсказанная эмоция.
     */
    public final String emotion;

    /**
     * Конструктор создания прогноза эмоции.
     *
     * @param h       Hue (оттенок).
     * @param s       Saturation (насыщенность).
     * @param l       Lightness (яркость).
     * @param emotion предсказанная эмоция.
     */
    public EmotionPrediction(int h, int s, int l, String emotion) {
        this.h       = h;
        this.s       = s;
        this.l       = l;
        this.emotion = emotion;
    }
}