package com.scamofty.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle //Hilt automatically injects it
) : ViewModel() {
    var currentNoteId: Int? = null

    init {
        savedStateHandle.get<Int>("noteId")?.let {noteId ->
            if(noteId != 1) {
                viewModelScope.launch {
                    //TODO: what is .also?
                    noteUseCases.getNote(noteId)?.also { note ->
                        currentNoteId = note.id
                        _noteTitle.value = noteTitle.value.copy(
                            text = note.title,
                            isHintVisible = false
                        )
                        _noteBody.value = noteBody.value.copy(
                            text = note.content,
                            isHintVisible = false
                        )
                        _noteColor.value = note.color
                    }
                }
            }
        }
    }
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

    fun onEvent(event: AddEditNoteEvent) {
        when(event) {
            is AddEditNoteEvent.EnteredTitle -> {
                _noteTitle.value = noteTitle.value.copy(
                    text = event.value
                )
            }
            //Make sure hint is hidden when selected
            is AddEditNoteEvent.ChangeTitleFocus -> {
                _noteTitle.value = noteTitle.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteTitle.value.text.isBlank()
                )
            }
            is AddEditNoteEvent.EnteredContent -> {
                _noteBody.value = noteBody.value.copy(
                    text = event.value
                )
            }
            //Make sure hint is hidden when selected
            is AddEditNoteEvent.ChangeContentFocus -> {
                _noteBody.value = noteBody.value.copy(
                    isHintVisible = !event.focusState.isFocused && noteBody.value.text.isBlank()
                )
            }
            //TODO: Why is copy not needed here?
            is AddEditNoteEvent.ChangeColor -> {
                _noteColor.value = event.color
            }
            //now use coroutine because you're saving to a database which can take longer
            is AddEditNoteEvent.SaveNote -> {
                viewModelScope.launch {
                    try {
                        noteUseCases.addNote(
                            Note(
                                title = noteTitle.value.text,
                                content = noteBody.value.text,
                                timestamp = System.currentTimeMillis(),
                                color = noteColor.value,
                                id = currentNoteId
                            )
                        )
                        _eventFlow.emit(UiEvent.SaveNote)
                    } catch(e: InvalidNoteException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                message = e.message ?: "Couldn't save note"
                            )
                        )
                    }
                }
            }
            else -> {
                //Do nothing
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
        object SaveNote: UiEvent()
    }

}