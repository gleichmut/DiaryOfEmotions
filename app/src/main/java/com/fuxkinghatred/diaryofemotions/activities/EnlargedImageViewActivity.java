package com.fuxkinghatred.diaryofemotions.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.constants.Constants;

/**
 * Активность с увеличенным изображением.
 */
public class EnlargedImageViewActivity extends AppCompatActivity {

    /**
     * Метод onCreate вызывается при создании Activity.
     *
     * @param savedInstanceState сохраненное состояние Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_enlarged_image_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.imageViewEnlarged),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom);
                    return insets;
                });

        ImageView imageView = findViewById(R.id.imageViewEnlarged);
        String imageUriString = getIntent().getStringExtra(Constants.Extras.EXTRA_IMAGE_URI);
        Uri imageUri = Uri.parse(imageUriString);
        Glide.with(this).load(imageUri).into(imageView);
    }
}