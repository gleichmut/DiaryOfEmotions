
package com.fuxkinghatred.diaryofemotions.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fuxkinghatred.diaryofemotions.R;
import com.fuxkinghatred.diaryofemotions.models.Note;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Адаптер для отображения списка заметок в RecyclerView.
 */
public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.NoteViewHolder> {
    /**
     * Список заметок для отображения.
     */
    private final List<Note> notes;
    /**
     * Слушатель нажатий на элемент списка.
     */
    private final OnItemClickListener onItemClick;
    /**
     * Слушатель удаления заметки.
     */
    private final OnNoteDeletedListener onNoteDeletedListener;

    /**
     * Конструктор адаптера.
     *
     * @param notes                 Список заметок для отображения.
     * @param onItemClick           Слушатель нажатий на элемент списка.
     * @param onNoteDeletedListener Слушатель удаления заметки.
     */
    public MyNotesAdapter(List<Note> notes,
                          OnItemClickListener onItemClick,
                          OnNoteDeletedListener onNoteDeletedListener) {
        this.notes                 = notes;
        this.onItemClick           = onItemClick;
        this.onNoteDeletedListener = onNoteDeletedListener;
    }

    /**
     * Форматирует timestamp в строку даты и времени.
     *
     * @param timestamp timestamp в миллисекундах.
     * @return Отформатированная строка даты и времени.
     */
    private String formatTimestampToString(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm",
                new Locale("ru"));
        return dateFormat.format(calendar.getTime());
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
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent,
                false);
        return new NoteViewHolder(view);
    }

    /**
     * Связывает данные с ViewHolder.
     *
     * @param holder   ViewHolder.
     * @param position Позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.dateTimeTextView.setText(formatTimestampToString(note.timestamp));
        holder.titleTextView.setText(note.title);
        holder.textTextView.setText(note.text);
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(note));
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return Количество элементов в списке.
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * Удаляет заметку из списка и уведомляет RecyclerView об изменениях.
     *
     * @param position Позиция заметки в списке.
     */
    public void removeAt(int position) {
        // Проверка позиции и наличия слушателя удаления
        if (position < notes.size() && position >= 0 && onNoteDeletedListener != null) {
            Note noteToRemove = notes.get(position);
            // Уведомление слушателя об удалении заметки
            onNoteDeletedListener.onNoteDeleted(position, noteToRemove);
        }
    }

    public void onDeletionFinished(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ViewHolder для элемента списка заметок.
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView для отображения даты и времени заметки.
         */
        final TextView dateTimeTextView;
        /**
         * TextView для отображения заголовка заметки.
         */
        final TextView titleTextView;
        /**
         * TextView для отображения текста заметки.
         */
        final TextView textTextView;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView View элемента списка.
         */
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTimeTextView = itemView.findViewById(R.id.datetimeNoteTextView);
            titleTextView    = itemView.findViewById(R.id.titleNoteTextView);
            textTextView     = itemView.findViewById(R.id.textNoteTextView);
        }
    }

    /**
     * Интерфейс для обработки нажатий на элемент списка.
     */
    public interface OnItemClickListener {
        /**
         * Метод вызывается при нажатии на заметку.
         *
         * @param note Нажатая заметка.
         */
        void onItemClick(Note note);
    }

    /**
     * Интерфейс для обработки удаления заметки.
     */
    public interface OnNoteDeletedListener {
        /**
         * Метод вызывается при удалении заметки.
         *
         * @param position Позиция удаленной заметки.
         * @param note     Удаленная заметка.
         */
        void onNoteDeleted(int position, Note note);
    }
}
