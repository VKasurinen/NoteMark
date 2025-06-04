package com.vkasurinen.notemark.auth.di

import com.vkasurinen.notemark.auth.presentation.landing.LandingViewModel
import com.vkasurinen.notemark.auth.presentation.register.RegisterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule = module {

    viewModelOf(::RegisterViewModel)
    viewModelOf(::LandingViewModel)

}