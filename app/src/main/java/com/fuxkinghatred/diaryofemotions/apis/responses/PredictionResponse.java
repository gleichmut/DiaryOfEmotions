package com.fuxkinghatred.diaryofemotions.apis.responses;

/**
 * Модель ответа с предсказанием эмоции.
 */
public class PredictionResponse {

    /**
     * Предсказанная эмоция.
     */
    private String predicted_emotion;

    /**
     * Геттер предсказанной эмоции.
     *
     * @return предсказанная эмоция.
     */
    public String getPredictedEmotion() {
        return predicted_emotion;
    }

    /**
     * Сеттер предсказанной эмоции.
     *
     * @param predictedEmotion предсказанная эмоция.
     */
    public void setPredictedEmotion(String predictedEmotion) {
        this.predicted_emotion = predictedEmotion;
    }
}