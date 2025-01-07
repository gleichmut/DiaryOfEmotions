
package com.fuxkinghatred.diaryofemotions.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.shared_preferences.SharedPreferencesUtils;
import com.fuxkinghatred.diaryofemotions.utils.ErrorUtils;
import com.fuxkinghatred.diaryofemotions.viewmodels.LoginViewModel;

/**
 * Активность авторизации.
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * Тег для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_LOGIN_ACTIVITY;

    /**
     * ViewModel для управления логикой входа.
     */
    private LoginViewModel loginViewModel;

    /**
     * TextView для "Забыли пароль?".
     */
    private TextView textViewForgotPassword;

    /**
     * EditText для ввода почты.
     */
    private EditText editTextEmail;

    /**
     * EditText для ввода пароля.
     */
    private EditText editTextPassword;

    /**
     * Кнопка для входа.
     */
    private Button buttonLogin;

    /**
     * Кнопка для перехода к регистрации.
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
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayoutLogin),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        // Инициализация view элементов
        initView();
        // Установка ViewModel
        setViewModelProviders();
        // Установка наблюдателей
        setObservers();
        // Установка слушателей
        setOnClickListeners();
    }

    /**
     * Устанавливает ViewModelProviders.
     */
    private void setViewModelProviders() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    /**
     * Устанавливает наблюдателей за LiveData.
     */
    private void setObservers() {
        // Наблюдатель за ошибками
        loginViewModel.getError().observe(this, error -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "loginViewModel.getError().observe is set"
            );
            if (error != null) {
                Log.e(
                        TAG,
                        "setObservers: " +
                                error
                );
                String localizedErrorMessage = ErrorUtils.getLocalizedErrorMessage(error);
                // Уведомляем пользователя об ошибке
                Toast.makeText(LoginActivity.this,
                        localizedErrorMessage,
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Наблюдатель за данными пользователя после авторизации
        loginViewModel.getUser().observe(this, firebaseUser -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "loginViewModel.getUser().observe is set"
            );
            Log.d(
                    TAG,
                    "setObservers: " +
                            "firebaseUser: " + firebaseUser
            );
            // Если пользователь авторизован
            if (firebaseUser != null) {
                Log.d(
                        TAG,
                        "setObservers: " +
                                "user auth"
                );
                // Сохраняем состояние авторизации в SharedPreferences
                SharedPreferencesUtils.setLoggedIn(this, true);

                // Переходим к списку заметок пользователя
                Intent intent = MyNotesActivity.newIntent(
                        LoginActivity.this,
                        firebaseUser.getUid());
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
        // Наблюдатель за статусом отправки письма для восстановления пароля
        loginViewModel.getPasswordResetSent().observe(this, isSent -> {
            Log.d(
                    TAG,
                    "setObservers: " +
                            "loginViewModel.getPasswordResetSent().observe is set"
            );
            // Если письмо было отправлено
            if (isSent) {
                Log.d(
                        TAG,
                        "setObservers: " +
                                R.string.reset_instructions_sent_to_email
                );
                // Уведомляем пользователя об этом
                Toast.makeText(this,
                        R.string.reset_instructions_sent_to_email,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Устанавливает слушателей.
     */
    private void setOnClickListeners() {
        // Слушатель нажатия на кнопку "Войти"
        buttonLogin.setOnClickListener(view -> {
            // Получаем email и пароль из EditText
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Проверяем, что поля не пустые
            if (!email.isEmpty() && !password.isEmpty())
                // Вызываем метод входа из ViewModel
                loginViewModel.login(email, password);
            else {
                Log.e(
                        TAG,
                        "setOnClickListeners: " +
                                R.string.check_null_fields
                );
                // Уведомляем пользователя об ошибке
                Toast.makeText(LoginActivity.this,
                        R.string.check_null_fields,
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Слушатель нажатия на кнопку "Регистрация"
        buttonRegister.setOnClickListener(view -> {
            Intent intent = RegistrationActivity.newIntent(LoginActivity.this);
            startActivity(intent);
        });

        // Слушатель нажатия на TextView "Забыли пароль?"
        textViewForgotPassword.setOnClickListener(view -> {
            // Получаем email из EditText
            String email = editTextEmail.getText().toString().trim();

            // Проверяем, что email не пустой
            if (TextUtils.isEmpty(email)) {
                Log.e(
                        TAG,
                        "setOnClickListeners: " +
                                R.string.enter_email
                );
                // Показываем тост с сообщением о необходимости ввести email
                Toast.makeText(this,
                        R.string.enter_email,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Проверяем, что email имеет правильный формат
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Логгирование для отладки, если email невалидный
                Log.e(
                        TAG,
                        "setOnClickListeners: " +
                                R.string.not_valid_email
                );
                //  Уведомляем пользователя об ошибке
                Toast.makeText(this,
                        R.string.not_valid_email,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Вызываем метод восстановления пароля из ViewModel
            loginViewModel.forgotPassword(email);
        });
    }

    /**
     * Инициализирует View элементы.
     */
    private void initView() {
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        editTextEmail          = findViewById(R.id.editTextEmail);
        editTextPassword       = findViewById(R.id.editTextPassword);
        buttonLogin            = findViewById(R.id.buttonLogin);
        buttonRegister         = findViewById(R.id.buttonRegister);
    }

    /**
     * Создает Intent для запуска LoginActivity для авторизации.
     *
     * @param context контекст
     * @return Intent
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}
