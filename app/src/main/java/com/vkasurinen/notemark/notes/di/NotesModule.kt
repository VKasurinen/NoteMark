package com.vkasurinen.notemark.notes.di


import com.vkasurinen.notemark.auth.data.api.AuthApi
import com.vkasurinen.notemark.auth.data.repository.AuthRepositoryImpl
import com.vkasurinen.notemark.auth.data.repository.EmailPatternValidator
import com.vkasurinen.notemark.auth.domain.PatternValidator
import com.vkasurinen.notemark.auth.domain.UserDataValidator
import com.vkasurinen.notemark.auth.domain.repository.AuthRepository
import com.vkasurinen.notemark.auth.presentation.landing.LandingViewModel
import com.vkasurinen.notemark.auth.presentation.login.LoginViewModel
import com.vkasurinen.notemark.auth.presentation.register.RegisterViewModel
import com.vkasurinen.notemark.core.data.auth.EncryptedSessionStorage
import com.vkasurinen.notemark.core.data.networking.HttpClientFactory
import com.vkasurinen.notemark.core.domain.SessionStorage
import com.vkasurinen.notemark.notes.presentation.NotesViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val notesModule = module {

    viewModelOf(::NotesViewModel)

}