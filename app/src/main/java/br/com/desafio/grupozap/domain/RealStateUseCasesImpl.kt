package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.FilterType
import br.com.desafio.grupozap.utils.PortalType
import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.utils.Constants
import br.com.desafio.grupozap.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealStateUseCasesImpl @Inject constructor(private val repository: DataRepository): RealStateUseCases {

    companion object {
        const val PAGE_SIZE = 20
    }

    private val cachedLegalStates: MutableList<RealState> = ArrayList()
    private val cachedFilteredStates: MutableList<RealState> = ArrayList()
    private var lastLegalStatesIndex = 0

    override suspend fun refreshCachedLegalStates() {
        repository.getAllRealStates().forEach {
            // verify if each real state is eligible for Zap or Viva Real or none
            val lat = it.address.geoLocation.location.lat
            val lon = it.address.geoLocation.location.lon
            if(lat != 0.0 && lon != 0.0) {
                it.apply {
                    val monthlyCondoFee = pricingInfos.monthlyCondoFee
                    val isInBoundBox = isOnBoundingBox(lat, lon)

                    if (pricingInfos.businessType == BusinessType.SALE.toString()
                        && usableAreas > 0) {
                        // Rules for sale
                        val pricePerSquareMeter = pricingInfos.price.toDouble()/usableAreas // convert to double
                        var zapMinSalePrice = Constants.GRUPO_ZAP_SALE_MIN_PRICE_ZAP
                        if (isInBoundBox) {
                            zapMinSalePrice -= zapMinSalePrice / 10 // If on bounding box is 10% cheaper
                        }

                        if (pricingInfos.price > Constants.GRUPO_ZAP_SALE_MAX_PRICE_VIVA_REAL && pricePerSquareMeter > 3500.0) {
                            portal = PortalType.ZAP
                        } else if (pricingInfos.price < zapMinSalePrice || pricePerSquareMeter <= 3500.0) {
                            portal = PortalType.VIVA_REAL
                        }

                        cachedLegalStates.add(this)
                    } else if (pricingInfos.businessType == BusinessType.RENTAL.toString()
                        && Utils.isValidNumber(monthlyCondoFee)) {
                        // Rules for rental
                        val rateCondoRentPrice: Double = pricingInfos.rentalTotalPrice.toDouble() / monthlyCondoFee!!.toInt()
                        var vivaRealMaxPrice = Constants.GRUPO_ZAP_RENTAL_MAX_PRICE_VIVA_REAL
                        if (isInBoundBox) {
                            vivaRealMaxPrice += vivaRealMaxPrice / 2
                        }

                        if (pricingInfos.rentalTotalPrice <= Constants.GRUPO_ZAP_RENTAL_MIN_PRICE_ZAP
                            && rateCondoRentPrice < 30) {
                            portal = PortalType.VIVA_REAL
                        } else if (pricingInfos.rentalTotalPrice > Constants.GRUPO_ZAP_RENTAL_MAX_PRICE_VIVA_REAL
                            || rateCondoRentPrice >= 30) {
                            portal = PortalType.ZAP
                        }

                        cachedLegalStates.add(this)
                    }
                }
            }
        }
    }

    private fun isOnBoundingBox(lat: Double, lon: Double): Boolean =
        lat <= Constants.GRUPO_ZAP_BOUNDING_BOX_MAX_LAT
                && lat >= Constants.GRUPO_ZAP_BOUNDING_BOX_MIN_LAT
                && lon <= Constants.GRUPO_ZAP_BOUNDING_BOX_MAX_LON
                && lon >= Constants.GRUPO_ZAP_BOUNDING_BOX_MIN_LON


    override fun clearFilter() {cachedFilteredStates.clear()}

    override suspend fun getByFilter(filterMap: Map<Int, String>, page: Int): List<RealState> {
        val startIndex = page * PAGE_SIZE
        val endIndex = startIndex + PAGE_SIZE

        if (filterMap.isEmpty()) {
            cachedFilteredStates.addAll(cachedLegalStates.subList(startIndex, endIndex))
        } else {
            if (cachedFilteredStates.size < endIndex+1 && lastLegalStatesIndex < cachedLegalStates.size-1) {
                val auxList = cachedLegalStates.subList(lastLegalStatesIndex, cachedLegalStates.size-1)
                auxList.forEachIndexed { index, state ->
                    var matchFilter = true

                    filterMap.keys.forEach { key ->
                        when(key) {
                            FilterType.LOCATION.filterValue ->
                                if (state.address.city != filterMap[key]
                                    && state.address.neighborhood != filterMap[key]) {
                                    matchFilter = false
                                    return@forEach
                                }
                            FilterType.TYPE.filterValue ->
                                if (state.pricingInfos.businessType != filterMap[key]) {
                                    matchFilter = false
                                    return@forEach
                                }
                            FilterType.PORTAL.filterValue ->
                                    if (state.portal.toString() != filterMap[key] && state.portal != PortalType.ALL) {
                                        matchFilter = false
                                        return@forEach
                                    }
                            FilterType.PRICE.filterValue ->
                                    if (state.pricingInfos.price > filterMap[key]?.toInt()?: 0) {
                                        matchFilter = false
                                        return@forEach
                                    }
                            FilterType.BEDROOMS.filterValue -> {
                                    val bedroom = filterMap[key]?.toInt()?:0
                                    if ((bedroom < 4 && state.bedrooms != bedroom) || state.bedrooms < bedroom) {
                                        matchFilter = false
                                        return@forEach
                                    }
                                }
                        }
                    }

                    if (matchFilter) {
                        cachedFilteredStates.add(state)
                        if (cachedFilteredStates.size > endIndex+1) {
                            lastLegalStatesIndex += index
                            return@forEachIndexed
                        }
                    }
                }
            }
        }

        return cachedFilteredStates
    }

    /* TODO remove if not needed
    override suspend fun getByFilter(filterMap: Map<Int, String>): List<RealState> {
        if (filterMap.isEmpty()) {
            return cachedLegalStates
        }

        if (cachedFilteredStates.isEmpty()) {
            cachedFilteredStates.addAll(cachedLegalStates)

            filterMap.keys.forEach {
                val statesToRemove = ArrayList<RealState>()

                when (it) {
                    FilterType.LOCATION.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            if ((state.address.city.isEmpty() || !state.address.city.equals(
                                    filterMap[it]
                                ))
                                && (state.address.neighborhood.isEmpty() || state.address.neighborhood.equals(
                                    filterMap[it]
                                ))
                            ) {
                                statesToRemove.add(state)
                            }
                        }
                    FilterType.TYPE.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            if (state.pricingInfos.businessType.equals(filterMap[it])) {
                                statesToRemove.add(state)
                            }
                        }
                    FilterType.PORTAL.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            if (state.portal.toString() != filterMap[it] && state.portal != PortalType.ALL) {
                                statesToRemove.add(state)
                            }
                        }
                    FilterType.PRICE.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            if (state.pricingInfos.price > filterMap[it]?.toInt()?: 0) {
                                statesToRemove.add(state)
                            }
                        }
                    FilterType.BEDROOMS.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            val bedroom = filterMap[it]?.toInt()?:0
                            if ((bedroom < 4 && state.bedrooms != bedroom) || state.bedrooms < bedroom) {
                                statesToRemove.add(state)
                            }
                        }
                }

                cachedFilteredStates.removeAll(statesToRemove)
            }
        }

        return cachedFilteredStates
    }*/
}