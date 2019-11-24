package br.com.desafio.grupozap.features.common

import android.os.Parcelable
import br.com.desafio.grupozap.utils.PortalType
import kotlinx.android.parcel.Parcelize

@Parcelize
class RealStateView(
    val usableAreas : Int,
    val parkingSpaces : Int,
    val images : List<String>,
    val bedrooms : Int,
    var portal: PortalType,
    val city : String,
    val neighborhood : String,
    val yearlyIptu : Int,
    val price : Int,
    val rentalTotalPrice : Int,
    val businessType : String,
    val monthlyCondoFee : Int
): Parcelable