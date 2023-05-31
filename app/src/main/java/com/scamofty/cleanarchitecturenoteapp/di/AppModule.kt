package com.scamofty.cleanarchitecturenoteapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.scamofty.cleanarchitecturenoteapp.feature_note.data.data_source.NoteDatabase
import com.scamofty.cleanarchitecturenoteapp.feature_note.data.repository.NoteRepositoryImpl
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.DeleteNote
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNotes
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(
                repository
            )
        )
    }
}