package com.vkasurinen.notemark.notes.di


import com.vkasurinen.notemark.notes.presentation.notes_details.DetailViewModel
import com.vkasurinen.notemark.notes.presentation.notes_overview.NotesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notesModule = module {

    viewModelOf(::NotesViewModel)
    viewModelOf(::DetailViewModel)

}