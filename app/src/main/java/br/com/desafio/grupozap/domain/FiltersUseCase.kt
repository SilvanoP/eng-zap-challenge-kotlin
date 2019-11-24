package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.features.common.FilterView

interface FiltersUseCase {
    suspend fun refreshCachedLegalStates(): Boolean
    suspend fun getFilters(): FilterView
    suspend fun saveFilters(location:String?, buy: Boolean, rental: Boolean, portal: String?, price: Int)
    fun getPrice(rate: Int, businessType: String?): Int
}