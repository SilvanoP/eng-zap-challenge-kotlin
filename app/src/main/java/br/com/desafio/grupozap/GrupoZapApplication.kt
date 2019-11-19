package br.com.desafio.grupozap

import android.app.Application
import br.com.desafio.grupozap.di.DaggerGrupoZapComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class GrupoZapApplication: Application(), HasAndroidInjector {

    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate() {
        super.onCreate()

        DaggerGrupoZapComponent.builder()
            .application(this)
            .build()
            .inject(this)
    }
}