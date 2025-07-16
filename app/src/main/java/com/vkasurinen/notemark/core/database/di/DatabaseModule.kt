package com.vkasurinen.notemark.core.database.di

import androidx.room.Room
import com.vkasurinen.notemark.core.database.NoteDatabase
import com.vkasurinen.notemark.core.database.RoomLocalNoteDataSource
import com.vkasurinen.notemark.core.domain.notes.LocalDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            NoteDatabase::class.java,
            "note.db"
        ).build()
    }
    single { get<NoteDatabase>().noteDao }
    single { get<NoteDatabase>().notePendingSyncDao }

    singleOf(::RoomLocalNoteDataSource).bind<LocalDataSource>()
}