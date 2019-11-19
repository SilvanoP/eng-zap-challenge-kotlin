package br.com.desafio.grupozap.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
object ContextModule {

    @Provides
    @JvmStatic
    fun providesContext(application: Application): Context {
        return application.applicationContext
    }
}