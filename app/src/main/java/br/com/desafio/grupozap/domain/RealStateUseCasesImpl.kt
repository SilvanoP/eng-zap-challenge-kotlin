package br.com.desafio.grupozap.domain

import br.com.desafio.grupozap.data.entities.BusinessType
import br.com.desafio.grupozap.data.entities.FilterType
import br.com.desafio.grupozap.data.entities.PortalType
import br.com.desafio.grupozap.data.entities.RealState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealStateUseCasesImpl @Inject constructor(private val repository: DataRepository): RealStateUseCases {

    val cachedLegalStates: MutableList<RealState> = ArrayList()
    val cachedFilteredStates: MutableList<RealState> = ArrayList()

    fun refreshCachedLegalStates() {
        GlobalScope.launch {
            repository.getAllRealStates().forEach {
                if(it.address.geoLocation.location.lat != 0
                    && it.address.geoLocation.location.lon != 0
                    && it.usableAreas > 0) {

                    //TODO missing business rules

                    val pricingInfos = it.pricingInfos
                    if ((pricingInfos.businessType.equals(BusinessType.SALE.toString()) && pricingInfos.price > 700000)
                        || (pricingInfos.businessType.equals(BusinessType.RENTAL.toString()) && pricingInfos.rentalTotalPrice > 4000) ) {

                        it.portal = PortalType.ZAP
                    } else if ((pricingInfos.businessType.equals(BusinessType.SALE.toString()) && pricingInfos.price < 600000)
                        || (pricingInfos.businessType.equals(BusinessType.RENTAL.toString()) && pricingInfos.rentalTotalPrice < 3500)) {
                        it.portal = PortalType.VIVA_REAL
                    } else {
                        it.portal = PortalType.ALL
                    }

                    cachedLegalStates.add(it)
                }
            }
        }
    }

    override fun clearFilter() {cachedFilteredStates.clear()}

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
                            if (!state.portal.toString().equals(filterMap[it]) && !state.portal.equals(PortalType.ALL)) {
                                statesToRemove.add(state)
                            }
                        }
                    FilterType.PRICE.filterValue ->
                        cachedFilteredStates.forEach { state ->
                            val pricingInfos = state.pricingInfos
                            val price =
                                pricingInfos.price + pricingInfos.monthlyCondoFee + pricingInfos.yearlyIptu
                            if (price > filterMap[it]?.toInt()?: 0) {
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
    }
}