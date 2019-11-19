package br.com.desafio.grupozap.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val city : String,
    val neighborhood : String,
    val geoLocation : GeoLocation
): Parcelable