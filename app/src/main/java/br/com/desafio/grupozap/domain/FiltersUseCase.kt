package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.utils.FilterType

interface FiltersUseCase {
    suspend fun refreshCachedLegalStates(): Boolean
    suspend fun getFilters(): Map<FilterType, String>
    suspend fun saveFilters(filterMap: Map<FilterType, String>)
}