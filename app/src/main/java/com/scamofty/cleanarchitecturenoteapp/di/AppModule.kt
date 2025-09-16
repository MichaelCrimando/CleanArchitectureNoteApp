package com.scamofty.cleanarchitecturenoteapp.di

import android.app.Application
import androidx.room.Room
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.local_source.NoteDatabase
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source.NoteApi
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.remote_source.NoteApiImpl
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.data.repository.NoteRepositoryImpl
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.repository.NoteRepository
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.AddNote
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.DeleteNote
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNote
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.GetNotes
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
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
    fun provideNoteRepository(db: NoteDatabase, api: NoteApi): NoteRepository {
        return NoteRepositoryImpl(db.noteDao, api)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
        )
    }

    @Provides
    @Singleton
    fun provideNoteApi(client: HttpClient): NoteApi {
        return NoteApiImpl(client)
    }
}