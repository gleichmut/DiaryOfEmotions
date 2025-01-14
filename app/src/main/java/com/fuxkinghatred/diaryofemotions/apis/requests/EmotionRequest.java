package com.fuxkinghatred.diaryofemotions.apis.requests;

/**
 * Модель запроса для получения эмоций.
 */
public class EmotionRequest {

    /**
     * Hue (оттенок).
     */
    private final float h;
    /**
     * Saturation (насыщенность).
     */
    private final float s;
    /**
     * Lightness (яркость).
     */
    private final float l;

    /**
     * Конструктор объекта запроса.
     *
     * @param h значение оттенка (hue).
     * @param s значение насыщенности (saturation).
     * @param l значение яркости (lightness).
     */
    public EmotionRequest(float h, float s, float l) {
        this.h = h;
        this.s = s;
        this.l = l;
    }

    /**
     * Геттер значения оттенка (hue).
     *
     * @return значение оттенка.
     */
    public float getH() {
        return h;
    }

    /**
     * Геттер значения насыщенности (saturation).
     *
     * @return значение насыщенности.
     */
    public float getS() {
        return s;
    }

    /**
     * Геттер значения яркости (lightness).
     *
     * @return значение яркости.
     */
    public float getL() {
        return l;
    }
}