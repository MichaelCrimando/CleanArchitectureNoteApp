package com.scamofty.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

data class NoteTextFieldState(
    val text: String = "",
    val hint: String = "Enter a title",
    val isHintVisible: Boolean = true
)