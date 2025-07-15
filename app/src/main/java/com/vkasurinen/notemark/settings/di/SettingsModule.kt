package com.vkasurinen.notemark.settings.di

import com.vkasurinen.notemark.settings.data.api.SettingsApi
import com.vkasurinen.notemark.settings.data.repository.SettingsRepositoryImpl
import com.vkasurinen.notemark.settings.domain.repository.SettingsRepository
import com.vkasurinen.notemark.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)

    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class

    single { SettingsApi(get()) }
}