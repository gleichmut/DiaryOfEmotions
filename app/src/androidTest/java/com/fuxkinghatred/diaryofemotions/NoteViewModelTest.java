package com.fuxkinghatred.diaryofemotions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.NoteViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class NoteViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private NoteRepository mockRepository;
    @Mock
    private Observer<List<Note>> mockObserver;
    private NoteViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new NoteViewModel(mockRepository);
    }

    @Test
    public void getAllNotesForCurrentUser_callsRepository() {
        MutableLiveData<List<Note>> notesLiveData = new MutableLiveData<>();
        notesLiveData.setValue(Collections.emptyList());
        when(mockRepository.getAllNotesForCurrentUser()).thenReturn(notesLiveData);
        viewModel.getAllNotesForCurrentUser().observeForever(mockObserver);
        verify(mockRepository).getAllNotesForCurrentUser();
        verify(mockObserver).onChanged(Collections.emptyList());
    }

    @Test
    public void getAllNotesForCurrentUser_returnsLiveDataFromRepository() {
        MutableLiveData<List<Note>> expectedLiveData = new MutableLiveData<>();
        List<Note> notes = Collections.singletonList(mock(Note.class));
        expectedLiveData.setValue(notes);
        when(mockRepository.getAllNotesForCurrentUser()).thenReturn(expectedLiveData);
        LiveData<List<Note>> actualLiveData = viewModel.getAllNotesForCurrentUser();
        actualLiveData.observeForever(mockObserver);
        assertEquals(expectedLiveData.getValue(), actualLiveData.getValue());
    }
}