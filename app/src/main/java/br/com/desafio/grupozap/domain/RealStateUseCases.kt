package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState

interface RealStateUseCases {
    fun clearFilter()
    suspend fun refreshCachedLegalStates()
    suspend fun getByFilter(filterMap: Map<Int, String>, page: Int): List<RealState>
}