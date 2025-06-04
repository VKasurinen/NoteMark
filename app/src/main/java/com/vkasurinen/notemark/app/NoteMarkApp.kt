package com.vkasurinen.notemark.app

import android.app.Application
import com.vkasurinen.notemark.app.di.appModule
import com.vkasurinen.notemark.auth.di.authModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class NoteMarkApp: Application() {

    val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@NoteMarkApp)
            modules(
                appModule,
                authModule
            )
        }
    }
}