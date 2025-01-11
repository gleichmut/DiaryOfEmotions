
package com.fuxkinghatred.diaryofemotions.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.constants.Constants;

import java.util.List;

/**
 * Адаптер для отображения изображений в RecyclerView.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    /**
     * Тэг для логирования.
     */
    private static final String TAG = Constants.Debug.TAG_IMAGE_ADAPTER;
    /**
     * Контекст приложения.
     */
    private final Context context;
    /**
     * Список URI изображений для отображения.
     */
    private final List<Uri> imageUris;
    /**
     * Слушатель нажатий на элементы списка.
     */
    private final OnItemClickListener listener;
    /**
     * Массив для хранения состояния загрузки изображений.
     */
    private final SparseBooleanArray isImageLoaded;
    /**
     * Массив для хранения состояния отображения изображения заглушки.
     */
    private final SparseBooleanArray isImageNotFound;
    /**
     * Текст с информацией об начале анализа фото.
     */
    private final TextView textViewAnalyze;

    /**
     * Конструктор адаптера.
     *
     * @param context   Контекст приложения.
     * @param imageUris Список URI изображений для отображения.
     * @param listener  Слушатель нажатий на элементы списка.
     */
    public ImageAdapter(Context context,
                        List<Uri> imageUris,
                        OnItemClickListener listener,
                        TextView textViewAnalyze) {
        this.context         = context;
        this.imageUris       = imageUris;
        this.listener        = listener;
        this.isImageLoaded   = new SparseBooleanArray();
        this.isImageNotFound = new SparseBooleanArray();
        this.textViewAnalyze = textViewAnalyze;
    }

    /**
     * Создает новый ViewHolder для элемента списка.
     *
     * @param parent   Родительский ViewGroup.
     * @param viewType Тип view.
     * @return Новый ViewHolder.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item,
                parent,
                false);
        return new ImageViewHolder(view);
    }

    /**
     * Связывает данные с ViewHolder.
     *
     * @param holder   ViewHolder.
     * @param position Позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri uri = imageUris.get(holder.getAdapterPosition());
        Glide.with(context)
                .load(uri)
                .error(R.drawable.not_found)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e,
                                                Object model,
                                                @NonNull Target<Drawable> target,
                                                boolean isFirstResource) {
                        Log.e(
                                TAG,
                                "onBindViewHolder: " +
                                        "GlideException: " + e + "\n" +
                                        "model: " + model + "\n" +
                                        "target: " + target + "\n"
                        );
                        Toast.makeText(context,
                                R.string.error_load_image,
                                Toast.LENGTH_SHORT).show();
                        isImageLoaded.put(holder.getAdapterPosition(), false);
                        isImageNotFound.put(holder.getAdapterPosition(), true);
                        if (textViewAnalyze != null) updateTextViewVisibility();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource,
                                                   @NonNull Object model,
                                                   Target<Drawable> target,
                                                   @NonNull DataSource dataSource,
                                                   boolean isFirstResource) {
                        isImageLoaded.put(holder.getAdapterPosition(), true);
                        isImageNotFound.put(holder.getAdapterPosition(), false);
                        if (textViewAnalyze != null) updateTextViewVisibility();
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            //Если изображение загружено, то даем клик
            if (isImageLoaded.get(holder.getAdapterPosition())) {
                listener.onItemClick(uri);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (isImageNotFound.get(holder.getAdapterPosition())) {
                //Если изображение НЕ загружено, то даем долгий клик
                listener.onItemLongClick(uri, holder.getAdapterPosition());
                return true;
            } else if (isImageLoaded.get(holder.getAdapterPosition())) {
                //Если изображение загружено, то даем долгий клик
                listener.onItemLongClick(uri, holder.getAdapterPosition());
                return true;
            }
            return false;
        });
        if (textViewAnalyze != null) updateTextViewVisibility();
    }

    /**
     * Скрытие текста с информацией о начале анализа фото.
     */
    private void updateTextViewVisibility() {
        boolean allImagesFailed = true;
        for (int i = 0; i < imageUris.size(); i++) {
            if (isImageLoaded.get(i)) {
                allImagesFailed = false;
                break;
            }
        }
        // Если все изображения не загрузились
        if (allImagesFailed) {
            // Тогда не даем возможности анализа
            textViewAnalyze.setVisibility(View.GONE);
        } else {
            // Если прогрузились, тогда даем возможность анализа
            textViewAnalyze.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return Количество элементов в списке.
     */
    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder для элемента списка.
     */
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        /**
         * ImageView для отображения изображения.
         */
        final ImageView imageView;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView View элемента списка.
         */
        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }

    /**
     * Интерфейс для обработки нажатий на элементы списка.
     */
    public interface OnItemClickListener {
        /**
         * Метод вызывается при нажатии на элемент списка.
         *
         * @param imageUri URI изображения.
         */
        void onItemClick(Uri imageUri);

        /**
         * Метод вызывается при долгом нажатии на элемент списка.
         *
         * @param imageUri URI изображения.
         * @param position Позиция элемента в списке.
         */
        void onItemLongClick(Uri imageUri, int position);
    }
}
