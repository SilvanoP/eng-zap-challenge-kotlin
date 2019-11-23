package br.com.desafio.grupozap.data.entities

import br.com.desafio.grupozap.utils.PortalType

data class RealState(
    val usableAreas : Int,
    val listingType : String,
    val createdAt : String,
    val listingStatus : String,
    val id : String,
    val parkingSpaces : Int,
    val updatedAt : String,
    val owner : Boolean,
    val images : List<String>,
    val address : Address,
    val bathrooms : Int,
    val bedrooms : Int,
    val pricingInfos : PricingInfos,
    @Transient
    var portal: PortalType = PortalType.ALL
)