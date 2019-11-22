package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.utils.FilterType

interface RealStateUseCases {
    suspend fun getByFilter(filterMap: Map<FilterType, String>): List<RealState>
    suspend fun getNextPage(page: Int): List<RealState>
}