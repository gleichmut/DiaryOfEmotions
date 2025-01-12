package com.fuxkinghatred.diaryofemotions.callbacks;

import android.graphics.Canvas;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.fuxkinghatred.diaryofemotions.adapters.MyNotesAdapter;
import com.fuxkinghatred.diaryofemotions.constants.Constants;
import com.fuxkinghatred.diaryofemotions.models.Note;

/**
 * • Обратный вызов для удаления элемента свайпом.
 */
public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    /**
     * Тег для логирования.
     */

    private static final String TAG = Constants.Debug.TAG_SWIPE_TO_DELETE_CALLBACK;
    /**
     * Адаптер для RecyclerView.
     */
    private final MyNotesAdapter adapter;

    /**
     * Конструктор для создания экземпляра обратного вызова.
     *
     * @param adapter адаптер для работы с элементами списка
     */
    public SwipeToDeleteCallback(MyNotesAdapter adapter) {
        // Поддерживаем только свайпы влево
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    /**
     * Метод для обработки перемещения элементов.
     *
     * @param recyclerView виджет RecyclerView, в котором находится перемещаемый элемент
     * @param viewHolder   держатель представления для перемещаемого элемента
     * @param target       целевая позиция, куда нужно переместить элемент
     * @return всегда возвращает false, поскольку мы не поддерживаем перемещение элементов
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * Метод, вызываемый при успешном свайпе элемента.
     *
     * @param viewHolder держатель представления для свайпа элемента
     * @param direction  направление свайпа
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Log.d(TAG, "onSwiped: position = " + position);
        if (position != RecyclerView.NO_POSITION) {
            Note noteToDelete = adapter.getNotes().get(position);
            if (noteToDelete == null) {
                Log.e(TAG, "onSwiped: noteToDelete is null at position " + position);
                return;
            }
            Log.d(TAG, "onSwiped: noteToDelete id = " + noteToDelete.getNoteId());
            adapter.getOnNoteDeletedListener().onNoteDeleted(noteToDelete.getNoteId(), noteToDelete);
        } else {
            Log.e(TAG, "onSwiped: position is RecyclerView.NO_POSITION");
        }
    }

    /**
     * Метод для рисования фона при свайпе.
     *
     * @param c                 холст для рисования
     * @param recyclerView      виджет RecyclerView, в котором происходит свайп
     * @param viewHolder        держатель представления для элемента, который свайпится
     * @param dX                смещение по оси X
     * @param dY                смещение по оси Y
     * @param actionState       состояние действия
     * @param isCurrentlyActive текущий активный статус
     */
    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}