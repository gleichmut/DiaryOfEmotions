
package com.fuxkinghatred.diaryofemotions.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
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
 * ViewModel для экрана анализа фотографии.
 */
public class AnalyzePhotoViewModel extends AndroidViewModel {
    /**
     * Тег для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_ANALYZE_PHOTO_VIEW_MODEL;
    /**
     * Интерфейс для выполнения HTTP запросов к API.
     */
    private              EmotionApi                  emotionApi;
    /**
     * LiveData для хранения предсказанной эмоции.
     */
    private final        MutableLiveData<String>     predictedEmotion        = new MutableLiveData<>();
    /**
     * LiveData для хранения сообщения об ошибке.
     */
    private final        MutableLiveData<String>     errorMessage            = new MutableLiveData<>();
    /**
     * LiveData для определения видимости поля ввода IP адреса.
     */
    private final        MutableLiveData<Boolean>    isIpAddressInputVisible = new MutableLiveData<>(true);
    /**
     * Текущий IP адрес сервера.
     */
    private              String                      currentIpAddress;
    /**
     * SharedPreferences для хранения IP адреса.
     */
    private final        SharedPreferences           sharedPreferences;
    /**
     * Ключ для сохранения IP адреса в SharedPreferences.
     */
    private static final String                      PREF_IP_ADDRESS         = "pref_ip_address";
    /**
     * Репозиторий для работы с предсказаниями эмоций.
     */
    private final        EmotionPredictionRepository repository;

    /**
     * Конструктор ViewModel.
     *
     * @param application  Контекст приложения.
     * @param repository Репозиторий для работы с предсказаниями эмоций.
     */
    public AnalyzePhotoViewModel(Application application, EmotionPredictionRepository repository) {
        super(application);
        sharedPreferences = application.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentIpAddress  = sharedPreferences.getString(PREF_IP_ADDRESS, null);
        createRetrofitInstance(getDefaultIpAddress());
        this.repository = repository;
        Log.d(
                TAG,
                "AnalyzePhotoViewModel: " +
                        "initialized with IP " + currentIpAddress
        );
    }

    /**
     * Получает IP адрес по умолчанию (для эмулятора или null).
     *
     * @return IP адрес по умолчанию.
     */
    private String getDefaultIpAddress() {
        if (currentIpAddress == null) {
            // Установка IP адреса для эмулятора
            currentIpAddress = isEmulator() ? "10.0.2.2" : null;
            Log.d(
                    TAG,
                    "getDefaultIpAddress: " +
                            "set default IP " + currentIpAddress
            );
        }
        return currentIpAddress;
    }

    /**
     * Возвращает LiveData, определяющую видимость поля ввода IP адреса.
     *
     * @return LiveData с видимостью поля ввода IP адреса.
     */
    public LiveData<Boolean> getIpAddressInputVisible() {
        return isIpAddressInputVisible;
    }

    /**
     * Проверяет, запущено ли приложение на эмуляторе.
     *
     * @return true, если приложение запущено на эмуляторе, false - в противном случае.
     */
    private boolean isEmulator() {
        // Проверка по отпечаткам сборки
        boolean isEmulator = android.os.Build.FINGERPRINT.contains("vbox")
                || android.os.Build.FINGERPRINT.contains("generic")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86");
        Log.d(
                TAG,
                "isEmulator: " + isEmulator
        );
        return isEmulator;
    }

    /**
     * Меняет IP адрес сервера и сохраняет его в SharedPreferences.
     *
     * @param ipAddress Новый IP адрес сервера.
     */
    public void changeIpAddress(String ipAddress) {
        createRetrofitInstance(ipAddress);
        saveIpAddress(ipAddress);
        isIpAddressInputVisible.setValue(false);
        Log.d(
                TAG,
                "changeIpAddress: " +
                        String.format("IP Address changed from %s to %s", currentIpAddress, ipAddress)
        );
        currentIpAddress = ipAddress;
    }

    /**
     * Сохраняет IP адрес в SharedPreferences.
     *
     * @param ipAddress IP адрес для сохранения.
     */
    private void saveIpAddress(String ipAddress) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_IP_ADDRESS, ipAddress);
        editor.apply();
        Log.d(
                TAG,
                "saveIpAddress" +
                        "saved IP " + ipAddress
        );
    }

    /**
     * Создает экземпляр Retrofit для выполнения HTTP запросов.
     *
     * @param ipAddress IP адрес сервера.
     */
    private void createRetrofitInstance(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            Log.d(
                    TAG,
                    "createRetrofitInstance" +
                            "IP address is not set. Cannot create Retrofit instance"
            );
            emotionApi = null;
            return;
        }
        Log.d(
                TAG,
                "createRetrofitInstance" +
                        "attempt with IP " + ipAddress
        );
        Retrofit retrofit;
        String baseUrl = "http://" + ipAddress + ":8000/";
        retrofit   = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        emotionApi = retrofit.create(EmotionApi.class);
        Log.d(
                TAG,
                "createRetrofitInstance" +
                        "created with IP " + ipAddress
        );
    }

    /**
     * Возвращает LiveData с предсказанной эмоцией.
     *
     * @return LiveData с предсказанной эмоцией.
     */
    public LiveData<String> getPredictedEmotion() {
        return predictedEmotion;
    }

    /**
     * Возвращает LiveData с сообщением об ошибке.
     *
     * @return LiveData с сообщением об ошибке.
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Выполняет запрос к API для предсказания эмоции.
     *
     * @param h Значение H (Hue) цветовой модели HSL.
     * @param s Значение S (Saturation) цветовой модели HSL.
     * @param l Значение L (Lightness) цветовой модели HSL.
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
                        isIpAddressInputVisible.setValue(false);
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
                    isIpAddressInputVisible.setValue(true);
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
                // Показ поля ввода IP адреса при ошибке
                isIpAddressInputVisible.setValue(true);
            }
        });
    }

    /**
     * Сохраняет предсказание эмоции в базу данных.
     *
     * @param h     Значение H (Hue) цветовой модели HSL.
     * @param s     Значение S (Saturation) цветовой модели HSL.
     * @param l     Значение L (Lightness) цветовой модели HSL.
     * @param emotion Название эмоции.
     * @return LiveData со статусом сохранения предсказания.
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
