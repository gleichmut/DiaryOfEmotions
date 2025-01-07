package com.fuxkinghatred.diaryofemotions.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.models.Emotion;

import java.util.List;

/**
 * Адаптер для отображения списка эмоций в Spinner.
 */
public class EmotionAdapter extends ArrayAdapter<Emotion> {
    /**
     * Контекст приложения.
     */
    private final Context       context;
    /**
     * ID ресурса layout для элемента списка.
     */
    private final int           resource;
    /**
     * Список эмоций для отображения.
     */
    private final List<Emotion> emotionsList;

    /**
     * Конструктор адаптера.
     *
     * @param context      Контекст приложения.
     * @param resource     ID ресурса layout для элемента списка.
     * @param emotionsList Список эмоций для отображения.
     */
    public EmotionAdapter(@NonNull Context context, int resource, List<Emotion> emotionsList) {
        super(context, resource, emotionsList);
        this.context      = context;
        this.resource     = resource;
        this.emotionsList = emotionsList;
    }

    /**
     * Метод для получения View элемента для отображения в списке.
     *
     * @param position    Позиция элемента в списке.
     * @param convertView View для переиспользования.
     * @param parent      Родительский ViewGroup.
     * @return View элемента списка.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    /**
     * Метод для получения View элемента для отображения в выпадающем списке (Spinner).
     *
     * @param position    Позиция элемента в списке.
     * @param convertView View для переиспользования.
     * @param parent      Родительский ViewGroup.
     * @return View элемента выпадающего списка.
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    /**
     * Общий метод для создания View элемента списка.
     *
     * @param position    Позиция элемента в списке.
     * @param convertView View для переиспользования.
     * @param parent      Родительский ViewGroup.
     * @return View элемента списка.
     */
    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            ImageView imageView = convertView.findViewById(R.id.emotionImageView);
            TextView textView = convertView.findViewById(R.id.emotionTextView);
            Emotion emotionItem = emotionsList.get(position);
            imageView.setImageResource(emotionItem.resourceId);
            textView.setText(emotionItem.emotionName);
        }
        return convertView;
    }
}