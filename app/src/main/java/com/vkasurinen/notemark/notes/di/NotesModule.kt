package com.vkasurinen.notemark.notes.di


import com.vkasurinen.notemark.auth.data.api.AuthApi
import com.vkasurinen.notemark.notes.data.api.NotesApi
import com.vkasurinen.notemark.notes.data.repository.NotesRepositoryImpl
import com.vkasurinen.notemark.notes.domain.repository.NotesRepository
import com.vkasurinen.notemark.notes.presentation.notes_details.DetailViewModel
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notesModule = module {
    viewModelOf(::NotesViewModel)

    viewModel { (noteId: String) ->
        DetailViewModel(
            notesRepository = get(),
            noteId = noteId
        )
    }
    single {NotesApi(get()) }

    singleOf(::NotesRepositoryImpl) bind NotesRepository::class
}