package com.vkasurinen.notemark.app.di

import com.vkasurinen.notemark.app.main.MainViewModel
import com.vkasurinen.notemark.app.NoteMarkApp
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<CoroutineScope> {
        (androidApplication() as NoteMarkApp).applicationScope
    }

    viewModel { MainViewModel(sessionStorage = get()) }

}