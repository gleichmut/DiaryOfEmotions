package com.fuxkinghatred.diaryofemotions.adapters;

import android.util.Log;
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
 * Адаптер списка заметок.
 */
public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.NoteViewHolder> {
    /**
     * Список заметок.
     */
    private List<Note> notes;
    /**
     * Слушатель нажатий на элемент списка.
     */
    private final OnItemClickListener onItemClick;
    /**
     * Слушатель удаления заметки.
     */
    private final OnNoteDeletedListener onNoteDeletedListener;

    /**
     * Получение списка заметок.
     *
     * @return список заметок
     */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Получение слушателя удаления заметки.
     *
     * @return слушатель удаления заметки
     */
    public OnNoteDeletedListener getOnNoteDeletedListener() {
        return onNoteDeletedListener;
    }

    /**
     * Конструктор адаптера.
     *
     * @param notes                 список заметок для отображения.
     * @param onItemClick           слушатель нажатий на элемент списка.
     * @param onNoteDeletedListener слушатель удаления заметки.
     */
    public MyNotesAdapter(List<Note> notes,
                          OnItemClickListener onItemClick,
                          OnNoteDeletedListener onNoteDeletedListener) {
        this.notes                 = notes;
        this.onItemClick           = onItemClick;
        this.onNoteDeletedListener = onNoteDeletedListener;
    }

    /**
     * Обновление списка заметок.
     *
     * @param newNotes обновленный список заметок.
     */
    public void updateData(List<Note> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    /**
     * Форматирует timestamp в строку даты и времени.
     *
     * @param timestamp timestamp в миллисекундах.
     * @return отформатированная строка даты и времени.
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
     * @param parent   родительский ViewGroup.
     * @param viewType тип view.
     * @return новый ViewHolder.
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
     * @param position позиция элемента в списке.
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.textViewDatetimeNote.setText(formatTimestampToString(note.timestamp));
        holder.textViewTitleNote.setText(note.title);
        holder.textViewTextNote.setText(note.text);
        if (note.getPhotos() != null) {
            Log.d("TAG", "onBindViewHolder: photos: " + note.getPhotos().size());
            String countImage = "Прикрепленных фото: " + note.getPhotos().size();
            holder.textViewImageCount.setText(countImage);
        } else {
            Log.d("TAG", "onBindViewHolder: photos: null");
            String countImage = "Нет прикрепленных фото";
            holder.textViewImageCount.setText(countImage);
        }
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(note));
    }

    /**
     * Возвращает количество элементов в списке.
     *
     * @return количество элементов в списке.
     */
    @Override
    public int getItemCount() {
        return notes.size();
    }

    /**
     * ViewHolder для элемента списка заметок.
     */
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        /**
         * TextView даты и времени заметки.
         */
        final TextView textViewDatetimeNote;
        /**
         * TextView заголовка заметки.
         */
        final TextView textViewTitleNote;
        /**
         * TextView текста заметки.
         */
        final TextView textViewTextNote;
        /**
         * TextView количества изображений в заметке.
         */
        final TextView textViewImageCount;

        /**
         * Конструктор ViewHolder.
         *
         * @param itemView View элемента списка.
         */
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDatetimeNote = itemView.findViewById(R.id.textViewDatetimeNote);
            textViewTitleNote    = itemView.findViewById(R.id.textViewTitleNote);
            textViewTextNote     = itemView.findViewById(R.id.textViewTextNote);
            textViewImageCount   = itemView.findViewById(R.id.textViewImageCount);
        }
    }

    /**
     * Интерфейс обработки нажатий на элемент списка.
     */
    public interface OnItemClickListener {
        /**
         * Клик на заметку.
         *
         * @param note нажатая заметка.
         */
        void onItemClick(Note note);
    }

    /**
     * Интерфейс удаления заметки.
     */
    public interface OnNoteDeletedListener {
        /**
         * Метод вызывается при удалении заметки.
         *
         * @param noteId ID удаленной заметки
         * @param note   удаленная заметка.
         */
        void onNoteDeleted(String noteId, Note note);
    }
}

