package com.fuxkinghatred.diaryofemotions.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

/**
 * ViewModel для выбора даты и времени.
 */
public class DateTimePickerViewModel extends ViewModel {
    /**
     * LiveData выбранной даты.
     */
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>();
    /**
     * LiveData выбранного времени.
     */
    private final MutableLiveData<Calendar> selectedTime = new MutableLiveData<>();
    /**
     * LiveData timestamp выбранной даты и времени в миллисекундах.
     */
    private final MutableLiveData<Long> timestamp = new MutableLiveData<>(0L);
    /**
     * LiveData флага валидности выбранных даты и времени.
     * Валидными считаются дата и время, которые меньше или равны текущим.
     */
    private final MutableLiveData<Boolean> isDateTimeValid = new MutableLiveData<>(false);

    /**
     * Возвращает LiveData выбранной даты.
     *
     * @return LiveData выбранной даты.
     */
    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    /**
     * Возвращает LiveData выбранного времени.
     *
     * @return LiveData выбранного времени.
     */
    public LiveData<Calendar> getSelectedTime() {
        return selectedTime;
    }

    /**
     * Возвращает LiveData флага валидности выбранных даты и времени.
     *
     * @return LiveData с флагом валидности.
     */
    public LiveData<Boolean> getIsDateTimeValid() {
        return isDateTimeValid;
    }

    /**
     * Устанавливает выбранную дату.
     *
     * @param date выбранная дата.
     */
    public void setDate(Calendar date) {
        selectedDate.setValue(date);
    }

    /**
     * Устанавливает выбранное время.
     *
     * @param time выбранное время.
     */
    public void setTime(Calendar time) {
        selectedTime.setValue(time);
    }

    /**
     * Обновляет timestamp на основе выбранных даты и времени.
     * Если выбрана дата, она устанавливается в Calendar.
     * Если выбрано время, оно устанавливается в Calendar.
     * После этого вычисляется timestamp и обновляется LiveData.
     * Запускает проверку валидности.
     *
     * @param calendar экземпляр Calendar, который будет обновлен.
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
     * @return timestamp выбранной даты и времени.
     */
    public long getDatetime() {
        return timestamp.getValue() == null ? Calendar.getInstance().getTimeInMillis() : timestamp.getValue();
    }
}