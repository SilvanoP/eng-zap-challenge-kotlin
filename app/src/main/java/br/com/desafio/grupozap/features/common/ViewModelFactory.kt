package br.com.desafio.grupozap.features.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.domain.ListRealStatesUseCases
import br.com.desafio.grupozap.domain.RealStateUseCases
import br.com.desafio.grupozap.features.detail.DetailViewModel
import br.com.desafio.grupozap.features.list.ListViewModel
import br.com.desafio.grupozap.features.search.SearchViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(private val filterUseCase: FiltersUseCase,
                                           private val listRealStatesUseCases: ListRealStatesUseCases,
                                           private val realStateUseCases: RealStateUseCases)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(SearchViewModel::class.java) ->
                    SearchViewModel(filterUseCase)
                isAssignableFrom(ListViewModel::class.java) ->
                    ListViewModel(listRealStatesUseCases)
                isAssignableFrom(DetailViewModel::class.java) ->
                    DetailViewModel(realStateUseCases)
                else ->
                    throw IllegalArgumentException("Unknown class: %s".format(this::class.java.simpleName))
            }
        } as T
}