package br.com.desafio.grupozap.di

import br.com.desafio.grupozap.domain.RealStateUseCases
import br.com.desafio.grupozap.domain.RealStateUseCasesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DomainModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesRealStateUseCases(realStateUseCasesImpl: RealStateUseCasesImpl): RealStateUseCases {
        return realStateUseCasesImpl
    }
}