package br.com.desafio.grupozap.features.search

import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.FilterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SearchViewModel (val useCase: FiltersUseCase): ViewModel(), LifecycleObserver {

    var filterMap = MutableLiveData<Map<FilterType, String>>() .apply {
        value = EnumMap(FilterType::class.java)
    }
    var untilPrice = MutableLiveData<String>().apply {
        value = filterMap.value!![FilterType.PRICE] ?: "0"
    }
    var loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var finishedSearch = MutableLiveData<Boolean>().apply {
        value = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun load() {
        loadingData.postValue(true)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                useCase.refreshCachedLegalStates()
            }
            val filter = useCase.getFilters()
            if (filter.isNotEmpty()) {
                filterMap.postValue(filter)
            }
            loadingData.postValue(!result)
        }
    }

    fun getPriceByRate(rate: Int, businessType: String) {
        val price = useCase.getPrice(rate, businessType)
        untilPrice.postValue(price.toString())
    }

    fun saveFilter(location:String?, buy: Boolean, rental: Boolean, portal: String?, price: Int) {
        val filters: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
        if (!location.isNullOrEmpty()) {
            filters[FilterType.LOCATION] = location
        }
        if (buy || rental) {
            val type = if (buy) {
                BusinessType.SALE.toString()
            }
            else {
                BusinessType.RENTAL.toString()
            }
            filters[FilterType.TYPE] = type
        }
        if (!portal.isNullOrEmpty()) {
            filters[FilterType.PORTAL] = portal
        }
        if (price > 0) {
            filters[FilterType.PRICE] = price.toString()
        }

        loadingData.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                useCase.saveFilters(filters)
            }
            finishedSearch.postValue(true)
        }
        loadingData.postValue(false)
    }
}