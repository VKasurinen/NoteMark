package com.vkasurinen.notemark.notes.di


import com.vkasurinen.notemark.core.data.notes.NotesApi
import com.vkasurinen.notemark.core.data.notes.OfflineFirstNoteRepository
import com.vkasurinen.notemark.core.domain.notes.NotesRepository
import com.vkasurinen.notemark.notes.presentation.notes_details.edit_details.EditDetailViewModel
import com.vkasurinen.notemark.notes.presentation.notes_details.reader_details.ReaderViewModel
import com.vkasurinen.notemark.notes.presentation.notes_details.view_details.ViewDetailViewModel
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesViewModel
import com.vkasurinen.notemark.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notesModule = module {
    viewModelOf(::NotesViewModel)
    viewModelOf(::SettingsViewModel)


    viewModelOf(::ReaderViewModel)

//    viewModel { (noteId: String) ->
//        ReaderViewModel(
//            notesRepository = get(),
//            noteId = noteId
//        )
//    }


    viewModel { (noteId: String) ->
        ViewDetailViewModel(
            notesRepository = get(),
            noteId = noteId
        )
    }

    viewModel { (noteId: String) ->
        EditDetailViewModel(
            notesRepository = get(),
            noteId = noteId
        )
    }
    single { NotesApi(get()) }

    singleOf(::OfflineFirstNoteRepository) bind NotesRepository::class
}