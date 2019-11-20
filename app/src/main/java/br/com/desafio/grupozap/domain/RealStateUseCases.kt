package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState

interface RealStateUseCases {
    fun clearFilter()
    suspend fun refreshCachedLegalStates(): Boolean
    suspend fun getByFilter(filterMap: Map<Int, String>): List<RealState>
    suspend fun getNextPage(page: Int): List<RealState>
}