package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.features.common.FilterView
import br.com.desafio.grupozap.features.common.RealStateView
import br.com.desafio.grupozap.utils.*
import br.com.desafio.grupozap.utils.Constants.PAGE_SIZE
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class UseCasesImpl @Inject constructor(private val repository: DataRepository): ListRealStatesUseCases,
    FiltersUseCase, RealStateUseCases {

    private val cachedLegalStates: MutableList<RealState> = ArrayList()
    private var filterMap: MutableMap<FilterType, String> = Collections.synchronizedMap(EnumMap<FilterType, String>(FilterType::class.java))
    private var cachedFilteredStates: MutableList<RealState> = ArrayList()
    private var lastLegalStatesIndex = 0
    private lateinit var selectedRealState: RealState

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

    override suspend fun getFilters(): FilterView {
        FilterType.values().forEach {filter ->
            val value: String? = repository.getFilter(filter.toString())
            if(value?.isNotEmpty() != false) {
                filterMap[filter] = value!!
            }
        }
        val rate = getRateFromPrice(filterMap[FilterType.PRICE], filterMap[FilterType.TYPE])

        return filterViewMapper(filterMap, rate)
    }

    private fun getRateFromPrice(price:String?, businessType: String?): Int {
        var rate = 0
        if (!price.isNullOrEmpty() && Utils.isValidNumber(price) && !businessType.isNullOrEmpty()) {
            val priceInt = price.toInt()
            rate = if (businessType == BusinessType.SALE.toString()) (priceInt-100000)/50000 else priceInt/1000
        }

        return rate
    }

    override suspend fun saveFilters(location:String?, buy: Boolean, rental: Boolean, portal: String?, price: Int) {
        filterMap.clear()
        repository.clearFilter()
        cachedFilteredStates.clear()

        if (!location.isNullOrEmpty()) {
            saveFilter(FilterType.LOCATION, location)
        }
        if (buy) {
            saveFilter(FilterType.TYPE, BusinessType.SALE.toString())
            saveFilter(FilterType.PRICE, getPrice(price, BusinessType.SALE.toString()).toString())
        } else if (rental) {
            saveFilter(FilterType.TYPE, BusinessType.RENTAL.toString())
            saveFilter(FilterType.PRICE, getPrice(price, BusinessType.RENTAL.toString()).toString())
        }
        if (!portal.isNullOrEmpty()) {
            saveFilter(FilterType.PORTAL, portal)
        }
    }

    private suspend fun saveFilter(filter: FilterType, value: String) {
        if (value.isNotEmpty() && value != "0") {
            filterMap[filter] = value
            repository.saveFilter(filter.toString(), value)
        }
    }

    override fun getPrice(rate: Int, businessType: String?): Int {
        if (!businessType.isNullOrEmpty() && rate > 0) {
            if (BusinessType.SALE.toString() == businessType) {
                return 100000 + (rate * 50000)
            }

            return (rate * 1000)
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

        var filteredStates: MutableList<RealStateView> = ArrayList()

        if (cachedFilteredStates.size >= endIndex) {
            filteredStates = cachedFilteredStates.map {
                realStateViewMapper(it)
            }.toMutableList()
        } else if(lastLegalStatesIndex < cachedLegalStates.size-1) {
            val auxList = cachedLegalStates.subList(lastLegalStatesIndex, cachedLegalStates.size)
            var auxListIndex = 0
            for (state in auxList) {
                var matchFilter = true

                for (key in filterMap.keys) {
                    when(key) {
                        FilterType.LOCATION ->
                            if ((state.address.city.isNullOrEmpty() && state.address.neighborhood.isNullOrEmpty())
                                || (state.address.city!!.toLowerCase(Locale.getDefault()) != filterMap[key]!!.toLowerCase(Locale.getDefault())
                                        && state.address.neighborhood!!.toLowerCase(Locale.getDefault()) != filterMap[key]!!.toLowerCase(Locale.getDefault()))) {
                                matchFilter = false
                            }
                        FilterType.TYPE ->
                            if (state.pricingInfos.businessType != filterMap[key]) {
                                matchFilter = false
                            }
                        FilterType.PORTAL ->
                            if (state.portal.toString() != filterMap[key] && filterMap[key] != PortalType.ALL.toString()) {
                                matchFilter = false
                            }
                        FilterType.PRICE -> {
                            val realStateBusinessType = state.pricingInfos.businessType
                            val realStatePrice =
                                if (realStateBusinessType == BusinessType.SALE.toString()) state.pricingInfos.price?: 0
                                else state.pricingInfos.rentalTotalPrice ?: 0
                            if (realStatePrice > filterMap[key]?.toInt()?: 0) {
                                matchFilter = false
                            }
                        }
                    }
                    if(!matchFilter)
                        break
                }

                if (matchFilter) {
                    cachedFilteredStates.add(state)
                    filteredStates.add(realStateViewMapper(state))
                }
                if (filteredStates.size >= PAGE_SIZE) {
                    lastLegalStatesIndex += auxListIndex
                    break
                }
                auxListIndex++
            }
        }

        return filteredStates
    }

    override fun selectRealState(index: Int): Boolean {
        if (cachedFilteredStates.size > index) {
            selectedRealState = cachedFilteredStates[index]
            return true
        }

        return false
    }

    override fun getSelectedRealState(): RealStateView {
        lastLegalStatesIndex = 0
        return realStateViewMapper(selectedRealState)
    }

    private val realStateViewMapper: (RealState) -> RealStateView = {
        RealState ->
        RealStateView(
            RealState.usableAreas,
            RealState.parkingSpaces,
            RealState.images,
            RealState.bedrooms,
            RealState.address.city ?: "",
            RealState.address.neighborhood ?: "",
            RealState.pricingInfos.yearlyIptu ?: 0,
            RealState.pricingInfos.price ?: 0,
            RealState.pricingInfos.rentalTotalPrice ?: 0,
            RealState.pricingInfos.businessType,
            RealState.pricingInfos.monthlyCondoFee ?: 0,
            RealState.address.geoLocation.location.lat,
            RealState.address.geoLocation.location.lon

        )
    }

    private val filterViewMapper: (Map<FilterType, String>, rate: Int) -> FilterView = {
        filterMap, rate ->
        FilterView(
            filterMap[FilterType.LOCATION] ?: "",
            filterMap[FilterType.TYPE] == BusinessType.SALE.toString(),
            filterMap[FilterType.PORTAL] ?: PortalType.ALL.toString(),
            filterMap[FilterType.PRICE] ?: "0",
            rate
        )
    }
}