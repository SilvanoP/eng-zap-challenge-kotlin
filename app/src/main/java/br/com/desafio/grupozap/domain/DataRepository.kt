package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState

interface DataRepository {
    suspend fun getAllRealStates(): List<RealState>
    suspend fun getFilter(filter: String): String?
    suspend fun saveFilter(filter: String, value: String): Boolean
    suspend fun clearFilter()
}