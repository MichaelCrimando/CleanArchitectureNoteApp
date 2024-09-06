package com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case

data class NoteUseCases(
    val getNotes: GetNotes,
    val deleteNote: DeleteNote,
    val addNote: AddNote,
    val addCloudNote: AddCloudNote,
    val getNote: GetNote,
)
