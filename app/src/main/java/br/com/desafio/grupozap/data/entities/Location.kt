package br.com.desafio.grupozap.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location (
    val lon : Int,
    val lat : Int
): Parcelable