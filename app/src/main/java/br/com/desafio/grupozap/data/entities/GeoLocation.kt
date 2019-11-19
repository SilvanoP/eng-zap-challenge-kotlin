package br.com.desafio.grupozap.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeoLocation (
    val precision : String,
    val location : Location
): Parcelable