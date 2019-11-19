package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState

interface DataRepository {
    suspend fun getAllRealStates(): List<RealState>
}