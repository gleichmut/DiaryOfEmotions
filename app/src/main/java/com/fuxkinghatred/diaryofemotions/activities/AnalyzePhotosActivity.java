package com.fuxkinghatred.diaryofemotions.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.adapters.EmotionAdapter;
import com.fuxkinghatred.diaryofemotions.callbacks.PaletteCallback;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.factories.AnalyzePhotoViewModelFactory;
import com.fuxkinghatred.diaryofemotions.models.Emotion;
import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.EmotionPredictionRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.AnalyzePhotoViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity анализа фото.
 */
public class AnalyzePhotosActivity extends AppCompatActivity {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_ANALYZE_PHOTO_ACTIVITY;

    /**
     * ScrollView для прокрутки контента.
     */
    private ScrollView scrollViewAnalyzePhoto;

    /**
     * Индикатор загрузки.
     */
    private ProgressBar progressBar;

    /**
     * Кнопка "Скорректировать".
     */
    private Button buttonCorrect;

    /**
     * Кнопка "Да".
     */
    private Button buttonYes;

    /**
     * Кнопка "Нет".
     */
    private Button buttonNo;

    /**
     * Кнопка "Закрыть".
     */
    private Button buttonClose;

    /**
     * Spinner для выбора эмоции.
     */
    private Spinner spinnerEmotions;

    /**
     * Layout исправления эмоционального состояния.
     */
    private LinearLayout linearLayoutCorrectEmotionalState;

    /**
     * Layout кнопки закрытия.
     */
    private LinearLayout linearLayoutClose;

    /**
     * Layout кнопок "Да/Нет".
     */
    private LinearLayout linearLayoutRight;

    /**
     * ImageView яркого цвета.
     */
    private ImageView imageViewVibrantColor;

    /**
     * ImageView светлого яркого цвета.
     */
    private ImageView imageViewLightVibrantColor;

    /**
     * ImageView темного яркого цвета.
     */
    private ImageView imageViewDarkVibrantColor;

    /**
     * ImageView приглушенного цвета.
     */
    private ImageView imageViewMuted;

    /**
     * ImageView темного приглушенного цвета.
     */
    private ImageView imageViewDarkMuted;

    /**
     * ImageView светлого приглушенного цвета.
     */
    private ImageView imageViewLightMuted;

    /**
     * ImageView анализируемого изображения.
     */
    private ImageView imageViewAnalyze;

    /**
     * ImageView доминантного цвета.
     */
    private ImageView imageViewDominantColor;

    /**
     * TextView предсказанной эмоции.
     */
    private TextView textViewPredictedEmotion;

    /**
     * ViewModel для AnalyzePhotoActivity.
     */
    private AnalyzePhotoViewModel analyzePhotoViewModel;

    /**
     * Массив значений цвета HSL.
     */
    private final float[] hsl = new float[3];

    /**
     * Предсказанная эмоция.
     */
    private String emotionPredicted;

    /**
     * Выбранная эмоция.
     */
    private String emotion;

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analyze_photo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollViewAnalyzePhoto),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        // Инициализация view элементов
        initViews();
        // Скрываем ScrollView и показываем ProgressBar, пока предсказывается
        scrollViewAnalyzePhoto.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        // Скрываем layout исправления эмоций
        linearLayoutCorrectEmotionalState.setVisibility(View.GONE);
        linearLayoutRight.setVisibility(View.GONE);
        // Скрываем кнопку закрытия
        buttonClose.setVisibility(View.GONE);
        // Загрузка изображения
        setImage();
        // Установка ViewModel
        setViewModelProvider();
        // Установка слушателей
        setListeners();
        // Установка наблюдателей
        setObservers();
    }

    /**
     * Устанавливает слушателей.
     */
    private void setListeners() {
        // Слушатель ImageView открывает изображение на весь экран
        imageViewAnalyze.setOnClickListener(view -> {
            Intent intent = new Intent(this, EnlargedImageViewActivity.class);
            intent.putExtra(Constants.Extras.EXTRA_IMAGE_URI,
                    getIntent().getStringExtra(Constants.Extras.EXTRA_IMAGE_URI));
            startActivity(intent);
        });

        buttonYes.setOnClickListener(view -> yesButtonClick());
        buttonNo.setOnClickListener(view -> noButtonClick());
        buttonClose.setOnClickListener(view -> finish());
        buttonCorrect.setOnClickListener(view -> correctButtonClick());

        // Если LinearLayout исправления эмоционального состояния не null, то настраиваем Spinner
        if (linearLayoutCorrectEmotionalState != null) {
            // Заполняем Spinner эмоциями
            setEmotionsToSpinner();
            // Слушатель выбора эмоции из Spinner
            spinnerEmotions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Получаем выбранную эмоцию и меняем ее имя
                    Emotion selectedEmotion = (Emotion) adapterView.getItemAtPosition(i);
                    emotion = changeEmotionName(selectedEmotion.emotionName);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }

    /**
     * Устанавливает наблюдателей для LiveData.
     */
    private void setObservers() {
        // Наблюдатель для предсказанной эмоции
        analyzePhotoViewModel.getPredictedEmotion().observe(this, predictedEmotion -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "analyzePhotoViewModel.getPredictedEmotion().observe is set"
            );
            Log.d(
                    TAG,
                    "setObservers: " +
                            "Predicted emotion: " + predictedEmotion
            );

            if (predictedEmotion != null) {
                // Отображаем предсказанную эмоцию
                String predictedEmotionText = getString(R.string.predicted_emotion) + " " + predictedEmotion;
                textViewPredictedEmotion.setText(predictedEmotionText);
                emotionPredicted = predictedEmotion;
                // Пока не установим предсказанную эмоцию, ничего не показываем
                progressBar.setVisibility(View.GONE);
                scrollViewAnalyzePhoto.setVisibility(View.VISIBLE);
                linearLayoutRight.setVisibility(View.VISIBLE);
            }
        });
        // Наблюдатель сообщений об ошибках
        analyzePhotoViewModel.getErrorMessage().observe(this, errorMessage -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "analyzePhotoViewModel.getErrorMessage().observe is set"
            );
            Log.e(
                    TAG,
                    "setObservers: " +
                            errorMessage
            );
            // Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    errorMessage,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Инициализирует ViewModel.
     */
    private void setViewModelProvider() {
        EmotionPredictionRepository repository = new EmotionPredictionRepository();
        AnalyzePhotoViewModelFactory factory = new AnalyzePhotoViewModelFactory(getApplication(), repository);
        analyzePhotoViewModel = new ViewModelProvider(this, factory).get(AnalyzePhotoViewModel.class);
    }

    /**
     * Меняет эмоцию на соответствующую в базе данных.
     */
    private String changeEmotionName(String emotion) {
        switch (emotion) {
            case "Радуюсь":
                return "радость";
            case "Грущу":
                return "грусть";
            case "Злюсь":
                return "гнев";
            case "Боюсь":
                return "страх";
            case "Удивляюсь":
                return "удивление";
            default:
                return null;
        }
    }

    /**
     * Находит доминирующие цвета на изображении с помощью Palette.
     */
    private void findDominantColor(Bitmap bitmap, final PaletteCallback callback) {
        Palette.from(bitmap).generate(palette -> {
            if (palette != null) {
                Log.d(
                        TAG,
                        "findDominantColor: " +
                                "Palette: " + palette
                );
                // Получение доминирующих цветов
                int vibrantColor = palette.getVibrantColor(Color.TRANSPARENT);
                int lightVibrantColor = palette.getLightVibrantColor(Color.TRANSPARENT);
                int darkVibrantColor = palette.getDarkVibrantColor(Color.TRANSPARENT);
                int muted = palette.getMutedColor(Color.TRANSPARENT);
                int darkMuted = palette.getDarkMutedColor(Color.TRANSPARENT);
                int lightMuted = palette.getLightMutedColor(Color.TRANSPARENT);

                Log.d(
                        TAG,
                        "vibrantColor: " + vibrantColor + "\n" +
                                " lightVibrantColor: " + lightVibrantColor + "\n" +
                                " darkVibrantColor: " + darkVibrantColor + "\n" +
                                " muted: " + muted + "\n" +
                                " darkMuted: " + darkMuted + "\n" +
                                " lightMuted: " + lightMuted + "\n"
                );
                // Заполняем фоны ImageView полученными цветами
                setImageViewBackground(imageViewVibrantColor, vibrantColor);
                setImageViewBackground(imageViewLightVibrantColor, lightVibrantColor);
                setImageViewBackground(imageViewDarkVibrantColor, darkVibrantColor);
                setImageViewBackground(imageViewMuted, muted);
                setImageViewBackground(imageViewDarkMuted, darkMuted);
                setImageViewBackground(imageViewLightMuted, lightMuted);
                setImageViewBackground(imageViewDominantColor, getDominantColorFromPalette(palette));

                // Вызываем коллбек с палитрой
                callback.onPaletteReady(palette);
            } else {
                Log.e(
                        TAG,
                        "findDominantColor: " +
                                "palette is null"
                );
                // Вызываем коллбек с null, если палитра не получена
                callback.onPaletteReady(null);
            }
        });
    }

    /**
     * Устанавливает фон ImageView.
     */
    private void setImageViewBackground(ImageView imageView, int color) {
        // Проверяем на прозрачность
        if (color != Color.TRANSPARENT) {
            imageView.setBackgroundColor(color);
        } else {
            // Если цвет прозрачный, убираем фон ImageView
            imageView.setBackground(null);
            // Если фон не установлен, то скрываем ImageView
            if (imageView.getBackground() == null)
                imageView.setVisibility(View.GONE);
        }
    }

    /**
     * Получает превалирующий цвет из палитры.
     */
    public int getDominantColorFromPalette(Palette palette) {
        return palette == null || palette.getSwatches().isEmpty() ?
                Color.TRANSPARENT : palette.getDominantColor(Color.TRANSPARENT);
    }

    /**
     * Обрабатывает нажатие кнопки "Да".
     */
    private void yesButtonClick() {
        linearLayoutCorrectEmotionalState.setVisibility(View.GONE);
        linearLayoutRight.setVisibility(View.GONE);

        if (hsl != null) {
            Log.d(TAG,
                    "yesButtonClick: " +
                            "h: " + hsl[0] + "\n" +
                            "s: " + hsl[1] + "\n" +
                            "l: " + hsl[2] + "\n"
            );

            // Сохраняем предсказание эмоции
            analyzePhotoViewModel.saveEmotionPrediction((int) hsl[0],
                            (int) hsl[1],
                            (int) hsl[2],
                            emotionPredicted)
                    .observe(this, isSaved -> {
                        Log.d(
                                TAG,
                                "yesButtonClick: " +
                                        "analyzePhotoViewModel.saveEmotionPrediction().observe is set"
                        );
                        if (!isSaved) {
                            Log.d(
                                    TAG,
                                    "yesButtonClick: " +
                                            R.string.prediction_saved
                            );
                            Toast.makeText(this,
                                    R.string.prediction_saved,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(
                                    TAG,
                                    "yesButtonClick: " +
                                            R.string.prediction_not_saved
                            );
                            Toast.makeText(this,
                                    R.string.prediction_not_saved,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        buttonClose.setVisibility(View.VISIBLE);
    }

    /**
     * Обрабатывает нажатие кнопки "Нет".
     */
    private void noButtonClick() {
        linearLayoutClose.setVisibility(View.GONE);
        linearLayoutRight.setVisibility(View.GONE);
        // Показываем LinearLayout после раздутия
        linearLayoutCorrectEmotionalState.setVisibility(View.VISIBLE);
    }

    /**
     * Обрабатывает нажатие кнопки "Скорректировать".
     */
    private void correctButtonClick() {
        if (hsl != null) {
            Log.d(TAG,
                    "correctButtonClick: " +
                            "h: " + hsl[0] + "\n" +
                            "s: " + hsl[1] + "\n" +
                            "l: " + hsl[2] + "\n"
            );

            // Сохраняем скорректированное предсказание эмоции
            analyzePhotoViewModel.saveEmotionPrediction((int) hsl[0],
                            (int) hsl[1],
                            (int) hsl[2],
                            emotion)
                    .observe(this, isSaved -> {
                        Log.d(
                                TAG,
                                "correctButtonClick: " +
                                        "analyzePhotoViewModel.saveEmotionPrediction().observe is set"
                        );
                        if (!isSaved) {
                            Log.d(
                                    TAG,
                                    "correctButtonClick: " +
                                            R.string.prediction_saved
                            );
                            Toast.makeText(this,
                                    R.string.prediction_saved,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(
                                    TAG,
                                    "correctButtonClick: " +
                                            R.string.prediction_not_saved
                            );
                            Toast.makeText(this,
                                    R.string.prediction_not_saved,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            finish();
        }
    }

    /**
     * Заполняет Spinner эмоциями.
     */
    private void setEmotionsToSpinner() {
        String[] emotions = getResources().getStringArray(R.array.emotions);
        List<Emotion> emotionsList = new ArrayList<>();
        for (String emotion : emotions) {
            int imageResource = getImageResourceForEmotion(emotion);
            emotionsList.add(new Emotion(emotion, imageResource));
        }
        EmotionAdapter adapter = new EmotionAdapter(this, R.layout.emotion_item, emotionsList);
        spinnerEmotions.setAdapter(adapter);
    }

    /**
     * Получает ресурс изображения для каждой эмоции.
     */
    private int getImageResourceForEmotion(String emotion) {
        switch (emotion) {
            case "Радуюсь":
                return R.drawable.happy;
            case "Грущу":
                return R.drawable.sad;
            case "Злюсь":
                return R.drawable.angry;
            case "Боюсь":
                return R.drawable.scared;
            case "Удивляюсь":
                return R.drawable.shocked;
            default:
                return android.R.drawable.stat_sys_warning;
        }
    }

    /**
     * Загружает изображение и устанавливает его в ImageView.
     */
    private void setImage() {
        String imageUriString = getIntent().getStringExtra(Constants.Extras.EXTRA_IMAGE_URI);
        if (imageUriString != null) {
            Uri imageUri = Uri.parse(imageUriString);
            Log.d(
                    TAG,
                    "setImage: " +
                            "imageUri: " + imageUri
            );

            Glide.with(this)
                    .asBitmap()
                    .load(imageUri)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            // Устанавливаем полученное изображение в ImageView
                            imageViewAnalyze.setImageBitmap(resource);
                            // Находим превалирующие цвета
                            findDominantColor(resource, palette -> {
                                if (palette != null) {
                                    // Получаем превалирующий цвет из палитры
                                    int color = getDominantColorFromPalette(palette);
                                    // Получаем HSL значения цвета
                                    // Возвращает h: 0-360, s: 0-1, l: 0-1
                                    androidx.core.graphics.ColorUtils.RGBToHSL(Color.red(color),
                                            Color.green(color),
                                            Color.blue(color), hsl);
                                    // Делаем s: 0-100, l: 0-100
                                    hsl[1] = hsl[1] * 100;
                                    hsl[2] = hsl[2] * 100;
                                    // Запускаем предсказание эмоции
                                    analyzePhotoViewModel.predictEmotion((int) hsl[0],
                                            (int) hsl[1],
                                            (int) hsl[2]);
                                    Log.d(TAG,
                                            "setImage: onResourceReady: " +
                                                    "h: " + hsl[0] + "\n" +
                                                    "s: " + hsl[1] + "\n" +
                                                    "l: " + hsl[2] + "\n"
                                    );
                                } else {
                                    Log.e(
                                            TAG,
                                            "setImage: " +
                                                    R.string.palette_is_null
                                    );
                                    Toast.makeText(AnalyzePhotosActivity.this,
                                            R.string.palette_is_null,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Log.e(
                                    TAG,
                                    "setImage:" +
                                            "onLoadFailed: " +
                                            errorDrawable
                            );
                            Toast.makeText(AnalyzePhotosActivity.this,
                                    R.string.error_loading_image,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Log.e(
                    TAG,
                    "setImage: " +
                            R.string.error_loading_image
            );
            //  Уведомляем пользователя об ошибке
            Toast.makeText(this,
                    R.string.error_loading_image,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Инициализирует View элементы.
     */
    private void initViews() {
        scrollViewAnalyzePhoto            = findViewById(R.id.scrollViewAnalyzePhoto);
        progressBar                       = findViewById(R.id.progressBar);
        buttonCorrect                     = findViewById(R.id.buttonCorrect);
        buttonYes                         = findViewById(R.id.buttonYes);
        buttonNo                          = findViewById(R.id.buttonNo);
        buttonClose                       = findViewById(R.id.buttonClose);
        linearLayoutCorrectEmotionalState = findViewById(R.id.linearLayoutCorrectEmotionalState);
        linearLayoutClose                 = findViewById(R.id.linearLayoutClose);
        linearLayoutRight                 = findViewById(R.id.linearLayoutRight);
        spinnerEmotions                   = findViewById(R.id.spinnerEmotions);
        textViewPredictedEmotion          = findViewById(R.id.textViewPredictedEmotion);
        imageViewVibrantColor             = findViewById(R.id.imageViewVibrantColor);
        imageViewLightVibrantColor        = findViewById(R.id.imageViewLightVibrantColor);
        imageViewDarkVibrantColor         = findViewById(R.id.imageViewDarkVibrantColor);
        imageViewMuted                    = findViewById(R.id.imageViewMuted);
        imageViewDarkMuted                = findViewById(R.id.imageViewDarkMuted);
        imageViewLightMuted               = findViewById(R.id.imageViewLightMuted);
        imageViewDominantColor            = findViewById(R.id.imageViewDominantColor);
        imageViewAnalyze                  = findViewById(R.id.imageViewAnalyze);
    }

    /**
     * Создает Intent для запуска AnalyzePhotosActivity.
     *
     * @param context  контекст
     * @param note     заметка
     * @param imageUri URI анализируемого фото
     * @return Intent
     */
    public static Intent newIntent(Context context, Note note, String imageUri) {
        Intent intent = new Intent(context, AnalyzePhotosActivity.class);
        intent.putExtra(Constants.Extras.EXTRA_CURRENT_NOTE, note);
        intent.putExtra(Constants.Extras.EXTRA_IMAGE_URI, imageUri);
        return intent;
    }
}
