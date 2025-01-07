package com.fuxkinghatred.diaryofemotions.apis.requests;

/**
 * Класс запроса для передачи данных об эмоции.
 */
public class EmotionRequest {

    private final float h;
    private final float s;
    private final float l;

    /**
     * Конструктор для создания объекта запроса.
     *
     * @param h значение оттенка (hue)
     * @param s значение насыщенности (saturation)
     * @param l значение яркости (lightness)
     */
    public EmotionRequest(float h, float s, float l) {
        this.h = h;
        this.s = s;
        this.l = l;
    }

    /**
     * Геттер для получения значения оттенка (hue).
     *
     * @return значение оттенка
     */
    public float getH() {
        return h;
    }

    /**
     * Геттер для получения значения насыщенности (saturation).
     *
     * @return значение насыщенности
     */
    public float getS() {
        return s;
    }

    /**
     * Геттер для получения значения яркости (lightness).
     *
     * @return значение яркости
     */
    public float getL() {
        return l;
    }
}