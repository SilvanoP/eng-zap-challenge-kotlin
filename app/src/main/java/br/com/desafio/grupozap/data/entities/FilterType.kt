package br.com.desafio.grupozap.data.entities

enum class FilterType(val filterValue: Int) {
    LOCATION(0),
    TYPE(1),
    PORTAL(2),
    PRICE(3),
    BEDROOMS(4)
}