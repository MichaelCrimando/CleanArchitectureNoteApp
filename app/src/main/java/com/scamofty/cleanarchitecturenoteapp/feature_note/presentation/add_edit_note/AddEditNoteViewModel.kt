package com.scamofty.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {
    //States are stored for cases of things updating like rotating the screen

    //State for current Title
    private val _noteTitle = mutableStateOf(NoteTextFieldState(
        hint = "Enter title...",
        isHintVisible = true
    ))
    val noteTitle: State<NoteTextFieldState> = _noteTitle


    //State for current Body
    private val _noteBody = mutableStateOf(NoteTextFieldState(
        hint = "Enter some content...",
        isHintVisible = true
    ))
    val noteBody: State<NoteTextFieldState> = _noteBody

    //State for current color
    private val _noteColor = mutableStateOf<Int>(Note.noteColors.random().toArgb())
    val noteColor: State<Int> = _noteColor

    //Use for one time events, more like a xml thing since JetPack Compose doesn't have 1 time events
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
    }

}