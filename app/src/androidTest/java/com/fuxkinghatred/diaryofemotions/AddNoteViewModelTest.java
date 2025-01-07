package com.fuxkinghatred.diaryofemotions;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.fuxkinghatred.diaryofemotions.models.Note;
import com.fuxkinghatred.diaryofemotions.repositories.NoteRepository;
import com.fuxkinghatred.diaryofemotions.viewmodels.AddNoteViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddNoteViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private NoteRepository mockRepository;
    @Mock
    private Observer<Note> mockObserver;
    private AddNoteViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        viewModel = new AddNoteViewModel(mockRepository);
    }

    @Test
    public void saveNote_callsRepository() {
        Note testNote = new Note();
        MutableLiveData<Note> noteLiveData = new MutableLiveData<>();
        noteLiveData.setValue(testNote);
        when(mockRepository.saveNote(testNote)).thenReturn(noteLiveData);
        viewModel.saveNote(testNote).observeForever(mockObserver);
        verify(mockRepository).saveNote(testNote);
        verify(mockObserver).onChanged(testNote);
    }

    @Test
    public void saveNote_returnsLiveDataFromRepository() {
        Note testNote = new Note();
        MutableLiveData<Note> expectedLiveData = new MutableLiveData<>();
        expectedLiveData.setValue(testNote);
        when(mockRepository.saveNote(testNote)).thenReturn(expectedLiveData);
        LiveData<Note> actualLiveData = viewModel.saveNote(testNote);
        actualLiveData.observeForever(mockObserver);
        assert (actualLiveData.getValue() == testNote);
    }
}