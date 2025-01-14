
package com.fuxkinghatred.diaryofemotions.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.utils.ErrorUtils;
import com.fuxkinghatred.diaryofemotions.viewmodels.RegistrationViewModel;

/**
 * Activity регистрации.
 */
public class RegistrationActivity extends AppCompatActivity {
    /**
     * Тег логирования.
     */
    private static final String TAG = Constants.Debug.TAG_REGISTRATION_ACTIVITY;

    /**
     * ViewModel для RegistrationActivity.
     */
    private RegistrationViewModel registrationViewModel;

    /**
     * Поле ввода имени пользователя.
     */
    private EditText editTextName;

    /**
     * Поле ввода почты.
     */
    private EditText editTextEmail;

    /**
     * Поле ввода пароля.
     */
    private EditText editTextPassword;

    /**
     * Кнопка регистрации.
     */
    private Button buttonRegister;

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayoutRegister),
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
        // Установка ViewModel
        setViewModelProvider();
        // Установка наблюдателей
        setObservers();

        // Установка слушателя кнопки регистрации
        buttonRegister.setOnClickListener(view -> {
            // Получение введенных данных
            String email = getTrimmedValue(editTextEmail);
            String password = getTrimmedValue(editTextPassword);
            String name = getTrimmedValue(editTextName);
            Log.d(
                    TAG,
                    "onCreate: " +
                            "email: " + email + "\n" +
                            "password: " + password + "\n" +
                            "name: " + name
            );
            // Валидация полей
            if (!validateName(name)) {
                Toast.makeText(RegistrationActivity.this,
                        R.string.invalid_name,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validateEmail(email)) {
                Toast.makeText(RegistrationActivity.this,
                        R.string.not_valid_email,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!validatePassword(password)) {
                Toast.makeText(RegistrationActivity.this,
                        R.string.weak_password,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Проверка на заполненность всех полей
            if (!email.isEmpty() && !password.isEmpty() && !name.isEmpty())
                // Вызов метода регистрации
                try {
                    registrationViewModel.register(email, password, name);
                } catch (Exception e) {
                    Log.e(
                            TAG,
                            "onCreate: " +
                                    getString(R.string.user_exists)
                    );
                    // Уведомляем пользователя об ошибке
                    Toast.makeText(RegistrationActivity.this,
                            getString(R.string.user_exists),
                            Toast.LENGTH_SHORT).show();
                }
            else {
                Log.e(
                        TAG,
                        "onCreate: " +
                                R.string.check_null_fields
                );
                // Уведомляем пользователя об ошибке
                Toast.makeText(RegistrationActivity.this,
                        R.string.check_null_fields,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Валидация имени.
     *
     * @param name имя
     * @return true - если не пустое, иначе false
     */
    private boolean validateName(String name) {
        return !name.isEmpty();
    }

    /**
     * Валидация почты.
     *
     * @param email почта
     * @return true - если соответствует регулярному выражению, иначе false
     */
    private boolean validateEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Валидация пароля.
     *
     * @param password пароль
     * @return true - если длина пароля больше 5 символов, иначе false
     */
    private boolean validatePassword(String password) {
        return password.length() >= 6;
    }

    /**
     * Инициализирует ViewModel.
     */
    private void setViewModelProvider() {
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
    }

    /**
     * Устанавливает наблюдателей для LiveData.
     */
    private void setObservers() {
        // Наблюдатель ошибок регистрации
        registrationViewModel.getError().observe(this, error -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "registrationViewModel.getError().observe is set"
            );

            if (error != null) {
                Log.e(
                        TAG,
                        "setObservers: " +
                                error
                );
                String localizedErrorMessage = ErrorUtils.getLocalizedErrorMessage(error);
                // Уведомляем пользователя об ошибке
                Toast.makeText(RegistrationActivity.this,
                        localizedErrorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдение за успешной регистрацией пользователя
        registrationViewModel.getUser().observe(this, firebaseUser -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "registrationViewModel.getUser().observe is set"
            );

            if (firebaseUser != null) {
                Log.d(
                        TAG,
                        "setObservers: " +
                                "user auth"
                );
                Toast.makeText(RegistrationActivity.this,
                        R.string.registration_successful,
                        Toast.LENGTH_SHORT).show();
                Intent intent = MyNotesActivity.newIntent(
                        RegistrationActivity.this,
                        firebaseUser.getUid()
                );
                startActivity(intent);
                finish();
            } else {
                Log.e(
                        TAG,
                        "setObservers: " +
                                "user not auth"
                );
            }
        });
    }

    /**
     * Получает текст из EditText и удаляет пробелы в начале и конце строки.
     *
     * @param editText EditText, из которого нужно получить текст
     * @return строка без пробелов в начале и конце
     */
    private String getTrimmedValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    /**
     * Инициализирует View элементы.
     */
    private void initViews() {
        editTextEmail    = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName     = findViewById(R.id.editTextName);
        buttonRegister   = findViewById(R.id.buttonRegister);
    }

    /**
     * Создает Intent для запуска RegistrationActivity.
     *
     * @param context контекст
     * @return Intent
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, RegistrationActivity.class);
    }
}
