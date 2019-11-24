package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.features.common.RealStateView

interface RealStateUseCases {
    fun getSelectedRealState(): RealStateView
}