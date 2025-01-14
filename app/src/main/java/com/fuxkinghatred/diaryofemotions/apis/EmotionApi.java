package com.fuxkinghatred.diaryofemotions.apis;

import com.fuxkinghatred.diaryofemotions.apis.requests.EmotionRequest;
import com.fuxkinghatred.diaryofemotions.apis.responses.PredictionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Интерфейс для взаимодействия с API предсказания эмоций.
 */
public interface EmotionApi {

    /**
     * Отправляет запрос на сервер для предсказания эмоции.
     *
     * @param emotionRequest объект запроса, содержащий данные для предсказания эмоции.
     * @return ответ сервера с результатом предсказания эмоции.
     */
    @POST("/predict_emotion")
    Call<PredictionResponse> predictEmotion(@Body EmotionRequest emotionRequest);
}