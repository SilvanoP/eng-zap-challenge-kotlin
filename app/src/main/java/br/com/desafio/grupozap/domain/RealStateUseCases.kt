package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.features.common.RealStateView

interface RealStateUseCases {
    suspend fun getNextPage(page: Int): List<RealStateView>
}