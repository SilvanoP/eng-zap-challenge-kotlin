package br.com.desafio.grupozap.features.common

import android.os.Parcelable
import br.com.desafio.grupozap.utils.PortalType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RealStateView(
    val usableAreas : Int,
    val parkingSpaces : Int,
    val images : List<String>,
    val bedrooms : Int,
    val city : String?,
    val neighborhood : String?,
    val yearlyIptu : Int,
    val price : Int,
    val rentalTotalPrice : Int,
    val businessType : String,
    val monthlyCondoFee : Int,
    val lon : Double,
    val lat : Double
): Parcelable