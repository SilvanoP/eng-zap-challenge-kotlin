package br.com.desafio.grupozap.features.common

data class FilterView(
    var location: String,
    var forSale: Boolean,
    var portal: String,
    var priceLabel: String,
    var priceRate: Int
)