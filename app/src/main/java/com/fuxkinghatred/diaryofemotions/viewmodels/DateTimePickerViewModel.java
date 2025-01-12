package com.fuxkinghatred.diaryofemotions.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

/**
 * ViewModel для управления выбором даты и времени.
 * Отвечает за хранение и обновление выбранной даты, времени и полного timestamp,
 * а также за проверку валидности выбранных даты и времени.
 */
public class DateTimePickerViewModel extends ViewModel {
    /**
     * LiveData, хранящая выбранную дату.
     */
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    /**
     * LiveData, хранящая выбранное время.
     */
    private final MutableLiveData<Calendar> selectedTime = new MutableLiveData<>();
    /**
     * LiveData, хранящая timestamp выбранной даты и времени в миллисекундах.
     */
    private final MutableLiveData<Long> timestamp = new MutableLiveData<>(0L);
    /**
     * LiveData, хранящая флаг, указывающий, является ли выбранная дата и время валидными.
     * Валидными считаются дата и время, которые меньше или равны текущим.
     */
    private final MutableLiveData<Boolean> isDateTimeValid = new MutableLiveData<>(false);

    /**
     * Возвращает LiveData с выбранной датой.
     *
     * @return LiveData с выбранной датой.
     */
    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    /**
     * Возвращает LiveData с выбранным временем.
     *
     * @return LiveData с выбранным временем.
     */
    public LiveData<Calendar> getSelectedTime() {
        return selectedTime;
    }

    /**
     * Возвращает LiveData с флагом, указывающим, является ли выбранная дата и время валидными.
     *
     * @return LiveData с флагом валидности.
     */
    public LiveData<Boolean> getIsDateTimeValid() {
        return isDateTimeValid;
    }

    /**
     * Устанавливает выбранную дату.
     *
     * @param date Выбранная дата.
     */
    public void setDate(Calendar date) {
        selectedDate.setValue(date);
    }

    /**
     * Устанавливает выбранное время.
     *
     * @param time Выбранное время.
     */
    public void setTime(Calendar time) {
        selectedTime.setValue(time);
    }

    /**
     * Обновляет timestamp на основе выбранных даты и времени.
     * Если выбрана дата, она устанавливается в Calendar.
     * Если выбрано время, оно устанавливается в Calendar.
     * После этого вычисляется timestamp и обновляется LiveData.
     * Также запускает проверку валидности.
     *
     * @param calendar Экземпляр Calendar, который будет обновлен.
     */
    public void updateDatetime(Calendar calendar) {
        if (selectedDate.getValue() != null) {
            calendar.setTime(selectedDate.getValue().getTime());
        }
        if (selectedTime.getValue() != null) {
            calendar.set(Calendar.HOUR_OF_DAY, selectedTime.getValue().get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, selectedTime.getValue().get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        timestamp.setValue(calendar.getTimeInMillis());
        updateDateTimeValidation();
    }

    /**
     * Обновляет флаг валидности даты и времени.
     * Сравнивает timestamp выбранной даты и времени с текущим timestamp.
     * Устанавливает флаг isDateTimeValid в true, если выбранная дата и время меньше или равны текущим.
     */
    private void updateDateTimeValidation() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        long currentTimestamp = now.getTimeInMillis();
        long selectedTimestamp = timestamp.getValue() == null ? currentTimestamp : timestamp.getValue();
        boolean isValid = selectedTimestamp <= currentTimestamp;
        isDateTimeValid.setValue(isValid);
    }

    /**
     * Возвращает timestamp выбранной даты и времени.
     *
     * @return Timestamp выбранной даты и времени.
     */
    public long getDatetime() {
        return timestamp.getValue() == null ? Calendar.getInstance().getTimeInMillis() : timestamp.getValue();
    }
}