package br.com.desafio.grupozap.di

import br.com.desafio.grupozap.features.common.MainActivity
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector

@Module(includes = [AndroidInjectionModule::class])
abstract class ActivityModule {

    @PerActivity
    @ContributesAndroidInjector
    abstract fun contributesMainActivity(): MainActivity
}