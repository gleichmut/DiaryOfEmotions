package com.fuxkinghatred.diaryofemotions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.fuxkinghatred.diaryofemotions.viewmodels.DateTimePickerViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Calendar;

@RunWith(MockitoJUnitRunner.class)
public class DateTimePickerViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private Observer<Calendar> calendarObserver;
    @Mock
    private Observer<Boolean> booleanObserver;
    private DateTimePickerViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new DateTimePickerViewModel();
        viewModel.getSelectedDate().observeForever(calendarObserver);
        viewModel.getSelectedTime().observeForever(calendarObserver);
        viewModel.getIsDateTimeValid().observeForever(booleanObserver);
    }

    @Test
    public void setDate_updatesLiveData() {
        Calendar calendar = Calendar.getInstance();
        viewModel.setDate(calendar);
        assertEquals(calendar, viewModel.getSelectedDate().getValue());
    }

    @Test
    public void setTime_updatesLiveData() {
        Calendar calendar = Calendar.getInstance();
        viewModel.setTime(calendar);
        assertEquals(calendar, viewModel.getSelectedTime().getValue());
    }

    @Test
    public void updateDatetime_onlyDateIsSet_updatesTimestamp() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        testCalendar.set(Calendar.MILLISECOND, 0);
        viewModel.setDate(testCalendar);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(viewModel.getDatetime());
        assertEquals(testCalendar.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(testCalendar.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(testCalendar.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
        assertEquals(0, result.get(Calendar.HOUR_OF_DAY));
        assertEquals(0, result.get(Calendar.MINUTE));
        assertEquals(0, result.get(Calendar.SECOND));
        assertEquals(0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void updateDatetime_onlyTimeIsSet_updatesTimestamp() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.set(Calendar.HOUR_OF_DAY, 10);
        testCalendar.set(Calendar.MINUTE, 30);
        testCalendar.set(Calendar.SECOND, 0);
        testCalendar.set(Calendar.MILLISECOND, 0);
        viewModel.setTime(testCalendar);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(viewModel.getDatetime());
        assertEquals(result.get(Calendar.YEAR), Calendar.getInstance().get(Calendar.YEAR));
        assertEquals(result.get(Calendar.MONTH), Calendar.getInstance().get(Calendar.MONTH));
        assertEquals(result.get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        assertEquals(10, result.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, result.get(Calendar.MINUTE));
        assertEquals(0, result.get(Calendar.SECOND));
        assertEquals(0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void updateDatetime_dateAndTimeSet_updatesTimestamp() {
        Calendar testCalendarDate = Calendar.getInstance();
        testCalendarDate.set(2024, Calendar.JANUARY, 1);
        Calendar testCalendarTime = Calendar.getInstance();
        testCalendarTime.set(Calendar.HOUR_OF_DAY, 10);
        testCalendarTime.set(Calendar.MINUTE, 30);
        testCalendarTime.set(Calendar.SECOND, 0);
        testCalendarTime.set(Calendar.MILLISECOND, 0);
        viewModel.setDate(testCalendarDate);
        viewModel.setTime(testCalendarTime);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(viewModel.getDatetime());
        assertEquals(testCalendarDate.get(Calendar.YEAR), result.get(Calendar.YEAR));
        assertEquals(testCalendarDate.get(Calendar.MONTH), result.get(Calendar.MONTH));
        assertEquals(testCalendarDate.get(Calendar.DAY_OF_MONTH), result.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, result.get(Calendar.HOUR_OF_DAY));
        assertEquals(30, result.get(Calendar.MINUTE));
        assertEquals(0, result.get(Calendar.SECOND));
        assertEquals(0, result.get(Calendar.MILLISECOND));
    }

    @Test
    public void updateDatetime_dateInFuture_isDateTimeNotValid() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.add(Calendar.DAY_OF_MONTH, 1);
        viewModel.setDate(testCalendar);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        assertFalse(viewModel.getIsDateTimeValid().getValue() == null
                || viewModel.getIsDateTimeValid().getValue());
    }

    @Test
    public void updateDatetime_dateInPast_isDateTimeValid() {
        Calendar testCalendar = Calendar.getInstance();
        testCalendar.add(Calendar.DAY_OF_MONTH, -1);
        viewModel.setDate(testCalendar);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        assertTrue(viewModel.getIsDateTimeValid().getValue() == null
                || viewModel.getIsDateTimeValid().getValue());
    }

    @Test
    public void updateDatetime_dateIsCurrent_isDateTimeValid() {
        Calendar testCalendar = Calendar.getInstance();
        viewModel.setDate(testCalendar);
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        assertTrue(viewModel.getIsDateTimeValid().getValue() == null
                || viewModel.getIsDateTimeValid().getValue());
    }

    @Test
    public void getDatetime_returnsTimestamp() {
        Calendar calendar = Calendar.getInstance();
        viewModel.updateDatetime(calendar);
        assertEquals(calendar.getTimeInMillis(), viewModel.getDatetime());
    }

    @Test
    public void initialValues() {
        assertNull(viewModel.getSelectedDate().getValue());
        assertNull(viewModel.getSelectedTime().getValue());
        assertEquals(0L, viewModel.getDatetime());
        assertFalse(viewModel.getIsDateTimeValid().getValue() == null
                || viewModel.getIsDateTimeValid().getValue());
    }
}
