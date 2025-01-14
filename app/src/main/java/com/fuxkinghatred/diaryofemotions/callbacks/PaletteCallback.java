package com.fuxkinghatred.diaryofemotions.callbacks;

import androidx.palette.graphics.Palette;

/**
 * Интерфейс обратного вызова для обработки палитры цветов.
 */
public interface PaletteCallback {

    /**
     * Вызывается, когда палитра готова.
     *
     * @param palette готовая палитра цветов.
     */
    void onPaletteReady(Palette palette);
}