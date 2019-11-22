package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.utils.FilterType

interface DataRepository {
    suspend fun getAllRealStates(): List<RealState>
    suspend fun getFilters(): Map<FilterType, String>
    suspend fun saveFilters(filterMap: Map<FilterType, String>)
}