package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.features.common.RealStateView
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.Constants
import br.com.desafio.grupozap.utils.Constants.PAGE_SIZE
import br.com.desafio.grupozap.utils.FilterType
import br.com.desafio.grupozap.utils.PortalType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class UseCasesImpl @Inject constructor(private val repository: DataRepository): RealStateUseCases, FiltersUseCase {

    private val cachedLegalStates: MutableList<RealState> = ArrayList()
    private var filterMap: MutableMap<FilterType, String> = Collections.synchronizedMap(EnumMap<FilterType, String>(FilterType::class.java))
    private var lastLegalStatesIndex = 0

    override suspend fun refreshCachedLegalStates(): Boolean {
        val result = repository.getAllRealStates()
        result.forEach {
            // verify if each real state is eligible for Zap or Viva Real or none
            val lat = it.address.geoLocation.location.lat
            val lon = it.address.geoLocation.location.lon
            if(lat != 0.0 && lon != 0.0) {
                it.apply {
                    val monthlyCondoFee = pricingInfos.monthlyCondoFee
                    val price = pricingInfos.price ?: 0
                    val rentalTotalPrice = pricingInfos.rentalTotalPrice ?: 0
                    val isInBoundBox = isOnBoundingBox(lat, lon)

                    if (pricingInfos.businessType == BusinessType.SALE.toString()
                        && usableAreas > 0) {
                        // Rules for sale
                        val pricePerSquareMeter = price.toDouble()/usableAreas // convert to double
                        var zapMinSalePrice = Constants.GRUPO_ZAP_SALE_MIN_PRICE_ZAP
                        if (isInBoundBox) {
                            zapMinSalePrice -= zapMinSalePrice / 10 // If on bounding box is 10% cheaper
                        }

                        if (price > Constants.GRUPO_ZAP_SALE_MAX_PRICE_VIVA_REAL && pricePerSquareMeter > 3500.0) {
                            portal = PortalType.ZAP
                        } else if (price < zapMinSalePrice || pricePerSquareMeter <= 3500.0) {
                            portal = PortalType.VIVA_REAL
                        }

                        cachedLegalStates.add(this)
                    } else if (pricingInfos.businessType == BusinessType.RENTAL.toString()) {
                        // Rules for rental
                        val rateCondoRentPrice: Double = price.toDouble() / monthlyCondoFee!!.toInt()
                        var vivaRealMaxPrice = Constants.GRUPO_ZAP_RENTAL_MAX_PRICE_VIVA_REAL
                        if (isInBoundBox) {
                            vivaRealMaxPrice += vivaRealMaxPrice / 2
                        }

                        if (rentalTotalPrice <= Constants.GRUPO_ZAP_RENTAL_MIN_PRICE_ZAP
                            && rateCondoRentPrice < 30) {
                            portal = PortalType.VIVA_REAL
                        } else if (rentalTotalPrice > Constants.GRUPO_ZAP_RENTAL_MAX_PRICE_VIVA_REAL
                            || rateCondoRentPrice >= 30) {
                            portal = PortalType.ZAP
                        }

                        cachedLegalStates.add(this)
                    }
                }
            }
        }

        if(cachedLegalStates.size > 0)
            return true

        return false
    }

    override suspend fun getFilters(): Map<FilterType, String> {
        FilterType.values().forEach {filter ->
            val value: String? = repository.getFilter(filter.toString())
            if(value?.isNotEmpty() != false) {
                filterMap[filter] = value!!
            }
        }

        return filterMap
    }

    override suspend fun saveFilters(filterMap: Map<FilterType, String>) {
        filterMap.keys.forEach {filter ->
            repository.saveFilter(filter.toString(), filterMap.getValue(filter))
        }
    }

    override fun getPrice(index: Int, businessType: String?): Int {
        if (!businessType.isNullOrEmpty() && index > 0) {
            if (BusinessType.SALE.toString() == businessType) {
                return 100000 + (index * 50000)
            }

            return (index * 1000)
        }

        return 0
    }

    private fun isOnBoundingBox(lat: Double, lon: Double): Boolean =
        lat <= Constants.GRUPO_ZAP_BOUNDING_BOX_MAX_LAT
                && lat >= Constants.GRUPO_ZAP_BOUNDING_BOX_MIN_LAT
                && lon <= Constants.GRUPO_ZAP_BOUNDING_BOX_MAX_LON
                && lon >= Constants.GRUPO_ZAP_BOUNDING_BOX_MIN_LON

    override suspend fun getNextPage(page: Int): List<RealStateView> {
        val startIndex = page * PAGE_SIZE
        var endIndex = startIndex + PAGE_SIZE
        if (endIndex > cachedLegalStates.size) {
            endIndex = cachedLegalStates.size
        }

        val cachedFilteredStates: MutableList<RealStateView> = ArrayList()

        if(cachedFilteredStates.size < endIndex && lastLegalStatesIndex < cachedLegalStates.size-1) {
            val auxList = cachedLegalStates.subList(lastLegalStatesIndex, cachedLegalStates.size)
            auxList.forEachIndexed { index, state ->
                var matchFilter = true

                filterMap.keys.forEach { key ->
                    when(key) {
                        FilterType.LOCATION ->
                            if (!filterMap[key].isNullOrEmpty() && state.address.city != filterMap[key]
                                && state.address.neighborhood != filterMap[key]) {
                                matchFilter = false
                                return@forEach
                            }
                        FilterType.TYPE ->
                            if (!filterMap[key].isNullOrEmpty() && state.pricingInfos.businessType != filterMap[key]) {
                                matchFilter = false
                                return@forEach
                            }
                        FilterType.PORTAL ->
                            if (!filterMap[key].isNullOrEmpty() && state.portal.toString() != filterMap[key] && state.portal != PortalType.ALL) {
                                matchFilter = false
                                return@forEach
                            }
                        FilterType.PRICE -> {
                            val realStateBusinessType = state.pricingInfos.businessType
                            val realStatePrice =
                                if (realStateBusinessType == BusinessType.SALE.toString()) state.pricingInfos.price?: 0
                                else state.pricingInfos.rentalTotalPrice ?: 0
                            if (!filterMap[key].isNullOrEmpty() && realStatePrice > getPrice(filterMap[key]?.toInt()?: 0, state.pricingInfos.businessType)) {
                                matchFilter = false
                                return@forEach
                            }
                        }
                        FilterType.BEDROOMS -> {
                            val bedroom = filterMap[key]?.toInt()?:10
                            if ((bedroom < 4 && state.bedrooms != bedroom) || state.bedrooms < bedroom) {
                                matchFilter = false
                                return@forEach
                            }
                        }
                    }
                }

                if (matchFilter) {
                    cachedFilteredStates.add(realStateViewMapper(state))
                    if (cachedFilteredStates.size > endIndex) {
                        lastLegalStatesIndex += index
                        return@forEachIndexed
                    }
                }
            }
        }

        return cachedFilteredStates
    }

    private val realStateViewMapper: (RealState) -> RealStateView = {
        RealState ->
        RealStateView(
            RealState.usableAreas,
            RealState.parkingSpaces,
            RealState.images,
            RealState.bedrooms,
            RealState.portal ?: PortalType.ALL,
            RealState.address.city ?: "",
            RealState.address.neighborhood ?: "",
            RealState.pricingInfos.yearlyIptu ?: 0,
            RealState.pricingInfos.price ?: 0,
            RealState.pricingInfos.rentalTotalPrice ?: 0,
            RealState.pricingInfos.businessType,
            RealState.pricingInfos.monthlyCondoFee ?: 0
        )
    }
}