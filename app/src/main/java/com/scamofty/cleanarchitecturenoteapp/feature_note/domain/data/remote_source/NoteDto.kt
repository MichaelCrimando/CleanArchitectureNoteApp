package com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source

import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.Note
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(
    val id: Int?,
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
) {
    // DTO -> Domain
    fun toDomain(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            color = color,
        )
    }
}

