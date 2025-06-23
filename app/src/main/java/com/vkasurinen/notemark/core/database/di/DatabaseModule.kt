package com.vkasurinen.notemark.core.database.di

import androidx.room.Room
import com.vkasurinen.notemark.core.database.NoteDatabase
import org.koin.android.ext.koin.androidApplication
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
}