package com.vkasurinen.notemark.auth.di


import com.vkasurinen.notemark.auth.data.repository.EmailPatternValidator
import com.vkasurinen.notemark.auth.domain.PatternValidator
import com.vkasurinen.notemark.auth.domain.UserDataValidator
import com.vkasurinen.notemark.auth.presentation.landing.LandingViewModel
import com.vkasurinen.notemark.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {

    single<PatternValidator> { EmailPatternValidator }
    singleOf(::UserDataValidator)

//    viewModelOf(::RegisterViewModel)
    viewModel { RegisterViewModel(get()) }
    viewModelOf(::LandingViewModel)

}