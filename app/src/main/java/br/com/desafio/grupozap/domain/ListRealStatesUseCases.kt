package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.features.common.RealStateView

interface ListRealStatesUseCases {
    suspend fun getNextPage(page: Int): List<RealStateView>
    fun selectRealState(index: Int): Boolean
}