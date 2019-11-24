package br.com.desafio.grupozap.di

import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.domain.ListRealStatesUseCases
import br.com.desafio.grupozap.domain.RealStateUseCases
import br.com.desafio.grupozap.domain.UseCasesImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DomainModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesListRealStateUseCases(useCasesImpl: UseCasesImpl): ListRealStatesUseCases {
        return useCasesImpl
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesFiltersUseCases(useCasesImpl: UseCasesImpl): FiltersUseCase {
        return useCasesImpl
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesREalStateUseCases(useCasesImpl: UseCasesImpl): RealStateUseCases {
        return useCasesImpl
    }
}