package com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

//(concrete Ktor calls)
class NoteApiImpl(
    private val client: HttpClient
) : NoteApi {

    override suspend fun getNotes(): List<NoteDto> {

        //TODO: PUT ACTUAL URL
        return client.get("https://your-api.com/notes").body()
    }

    override suspend fun getNoteById(id: Int): NoteDto {
        //TODO: PUT ACTUAL URL
        return client.get("https://your-api.com/notes/$id").body()
    }

    override suspend fun insertNote(note: NoteDto) {
        //TODO: PUT ACTUAL URL
        client.post("https://your-api.com/notes") {
            contentType(ContentType.Application.Json)
            setBody(note)
        }
    }

    override suspend fun deleteNote(id: Int) {        //TODO: PUT ACTUAL URL
        client.delete("https://your-api.com/notes/$id")
    }
}