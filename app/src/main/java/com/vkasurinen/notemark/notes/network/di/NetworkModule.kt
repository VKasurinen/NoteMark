package com.vkasurinen.notemark.notes.network.di

import com.vkasurinen.notemark.core.domain.notes.RemoteDataSource
import com.vkasurinen.notemark.notes.network.RemoteNoteDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    singleOf(::RemoteNoteDataSource).bind<RemoteDataSource>()
}