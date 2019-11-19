package br.com.desafio.grupozap.data.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class PricingInfos (
    val yearlyIptu : Int,
    val price : Int,
    val rentalTotalPrice : Int,
    val businessType : String,
    val monthlyCondoFee : String?
): Parcelable