package br.com.desafio.grupozap.di

import android.app.Application
import android.content.Context
import br.com.desafio.grupozap.GrupoZapApplication
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ContextModule {

    @Singleton
    @Binds
    abstract fun providesContext(application: GrupoZapApplication): Context
}