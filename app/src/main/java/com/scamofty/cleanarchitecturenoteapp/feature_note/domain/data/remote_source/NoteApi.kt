package com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source

//(Ktor interface/service)
interface NoteApi {
    suspend fun getNotes(): List<NoteDto>
    suspend fun getNoteById(id: Int): NoteDto
    suspend fun insertNote(note: NoteDto)
    suspend fun deleteNote(id: Int)
    //suspend fun refreshNotes()
}