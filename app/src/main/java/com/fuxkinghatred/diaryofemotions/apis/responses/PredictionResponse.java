package com.fuxkinghatred.diaryofemotions.apis.responses;

/**
 * Класс ответа с предсказанием эмоции.
 */
public class PredictionResponse {

    private String predicted_emotion;

    /**
     * Геттер для получения предсказанной эмоции.
     *
     * @return предсказанная эмоция
     */
    public String getPredictedEmotion() {
        return predicted_emotion;
    }

    /**
     * Сеттер для установки предсказанной эмоции.
     *
     * @param predictedEmotion строка с предсказанной эмоцией
     */
    public void setPredictedEmotion(String predictedEmotion) {
        this.predicted_emotion = predictedEmotion;
    }
}