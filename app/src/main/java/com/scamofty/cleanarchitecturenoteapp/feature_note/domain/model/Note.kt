package com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source.NoteDto
import com.scamofty.cleanarchitecturenoteapp.ui.theme.*

@Entity
data class Note(
    @PrimaryKey val id: Int? = null,
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    val cachedAt: Long = System.currentTimeMillis(),
    val isInCloud: Boolean = false
) {
    companion object {
        val noteColors = listOf(RedOrange, LightGreen, Violet, BabyBlue, RedPink)
    }

    fun toDto(): NoteDto {
        return NoteDto(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            color = color,
        )
    }
}

    class InvalidNoteException(message: String) : Exception(message)
