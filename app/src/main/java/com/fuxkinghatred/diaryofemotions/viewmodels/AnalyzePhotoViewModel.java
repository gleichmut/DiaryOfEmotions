
package com.fuxkinghatred.diaryofemotions.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fuxkinghatred.diaryofemotions.apis.EmotionApi;
import com.fuxkinghatred.diaryofemotions.apis.requests.EmotionRequest;
import com.fuxkinghatred.diaryofemotions.apis.responses.PredictionResponse;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.repositories.EmotionPredictionRepository;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewModel для AnalyzePhotoActivity.
 */
public class AnalyzePhotoViewModel extends AndroidViewModel {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_ANALYZE_PHOTO_VIEW_MODEL;
    /**
     * Интерфейс для взаимодействия с API предсказания эмоций.
     */
    private EmotionApi emotionApi;
    /**
     * LiveData предсказанной эмоции.
     */
    private final MutableLiveData<String> predictedEmotion = new MutableLiveData<>();
    /**
     * LiveData сообщения об ошибке.
     */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    /**
     * Репозиторий для работы с предсказаниями эмоций.
     */
    private final EmotionPredictionRepository repository;

    /**
     * Конструктор ViewModel.
     *
     * @param application контекст приложения.
     * @param repository  репозиторий для работы с предсказаниями эмоций.
     */
    public AnalyzePhotoViewModel(Application application, EmotionPredictionRepository repository) {
        super(application);
        createRetrofitInstance();
        this.repository = repository;
    }

    /**
     * Создает экземпляр Retrofit для выполнения HTTP запросов.
     */
    private void createRetrofitInstance() {
        Retrofit retrofit;
        retrofit   = new Retrofit.Builder()
                .baseUrl(Constants.Urls.BASE_URL_RETROFIT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        emotionApi = retrofit.create(EmotionApi.class);
        Log.d(
                TAG,
                "createRetrofitInstance" +
                        "created with URL " + Constants.Urls.BASE_URL_RETROFIT
        );
    }

    /**
     * Возвращает LiveData предсказанной эмоцией.
     *
     * @return LiveData предсказанной эмоцией.
     */
    public LiveData<String> getPredictedEmotion() {
        return predictedEmotion;
    }

    /**
     * Возвращает LiveData сообщения об ошибке.
     *
     * @return LiveData сообщения об ошибке.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Выполняет запрос к API для предсказания эмоции.
     *
     * @param h Hue (оттенок).
     * @param s Saturation (насыщенность).
     * @param l Lightness (яркость).
     */
    public void predictEmotion(int h, int s, int l) {
        if (emotionApi == null) {
            Log.d(
                    TAG,
                    "predictEmotion: " +
                            "predictEmotion: emotionApi is null, IP is not valid"
            );
            errorMessage.setValue("IP address is not set or not valid");
            return;
        }
        Log.d(
                TAG,
                "predictEmotion: " +
                        "predictEmotion:  h = " + h + " s = " + s + " l = " + l
        );
        // Создание запроса к API
        EmotionRequest emotionRequest = new EmotionRequest(h, s, l);
        Call<PredictionResponse> call = emotionApi.predictEmotion(emotionRequest);
        // Выполнение асинхронного запроса
        call.enqueue(new Callback<>() {
            @Override
            // Обработка успешного ответа
            public void onResponse(@NonNull Call<PredictionResponse> call,
                                   @NonNull Response<PredictionResponse> response) {
                Log.d(
                        TAG,
                        "predictEmotion: " +
                                "code = " + response.code()
                );
                // Проверка успешности ответа
                if (response.isSuccessful()) {
                    PredictionResponse predictionResponse = response.body();
                    if (predictionResponse != null) {
                        // Установка предсказанной эмоции в LiveData
                        predictedEmotion.setValue(predictionResponse.getPredictedEmotion());
                        Log.d(
                                TAG,
                                "predictEmotion: " +
                                        "success, emotion = " + predictionResponse.getPredictedEmotion()
                        );
                    } else {
                        Log.e(
                                TAG,
                                "predictEmotion: " +
                                        "Response body is null"
                        );
                        errorMessage.setValue("Response body is null");
                    }
                } else {
                    Log.e(
                            TAG,
                            "predictEmotion: " +
                                    "Server error: " + response.code() + " " + response.message()
                    );
                    errorMessage.setValue("Server error: " + response.code() + " " + response.message());
                }
            }

            // Обработка ошибки при выполнении запроса
            @Override
            public void onFailure(@NonNull Call<PredictionResponse> call, @NonNull Throwable t) {
                Log.e(
                        TAG,
                        "predictEmotion: " +
                                "Network error: " + t.getMessage()
                );
                String message = "Network error: " + t.getMessage();

                if (t instanceof ConnectException) {
                    message = "Не удалось подключиться к серверу. Проверьте IP адрес.";
                    Log.e(
                            TAG,
                            "predictEmotion: " +
                                    "ConnectException - " + t.getMessage()
                    );
                } else if (t instanceof SocketTimeoutException) {
                    message = "Превышено время ожидания ответа от сервера. Проверьте IP адрес.";
                    Log.e(
                            TAG,
                            "predictEmotion: " +
                                    "SocketTimeoutException - " + t.getMessage()
                    );
                } else if (t instanceof IOException) {
                    message = "Ошибка ввода вывода";
                    Log.e(
                            TAG,
                            "predictEmotion: " +
                                    "IOException - " + t.getMessage()
                    );
                }
                // Установка сообщения об ошибке в LiveData
                errorMessage.setValue(message);
            }
        });
    }

    /**
     * Сохраняет предсказание эмоции в базу данных.
     *
     * @param h       Hue (оттенок).
     * @param s       Saturation (насыщенность).
     * @param l       Lightness (яркость).
     * @param emotion предсказанная эмоция.
     * @return LiveData статуса сохранения предсказания.
     */
    public LiveData<Boolean> saveEmotionPrediction(int h, int s, int l, String emotion) {
        Log.d(
                TAG,
                "saveEmotionPrediction: " +
                        "h = " + h + " s = " + s + " l = " + l + " emotion = " + emotion
        );
        return repository.saveEmotionPrediction(h, s, l, emotion);
    }
}
