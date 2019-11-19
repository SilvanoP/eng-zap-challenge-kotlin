package br.com.desafio.grupozap.di

import br.com.desafio.grupozap.GrupoZapApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(DataModule::class,
    ContextModule::class,
    AndroidSupportInjectionModule::class))
interface GrupoZapComponent: AndroidInjector<GrupoZapApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: GrupoZapApplication): Builder

        fun build(): GrupoZapComponent
    }

    override fun inject(instance: GrupoZapApplication)
}