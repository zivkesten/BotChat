package com.zk.lemopoc

import android.app.Application
import com.zk.lemopoc.backend.serverModule
import com.zk.lemopoc.di.ViewModelsModule
import com.zk.lemopoc.features.chat.repositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BotChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@BotChatApplication)
            modules(listOf(
				ViewModelsModule.modules,
                serverModule,
                repositoryModule
            ))
        }
    }
}
