package br.com.desafio.grupozap.features.search

import android.app.Application
import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.FilterType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        GlobalScope.launch {
            val result = useCase.refreshCachedLegalStates()
        }

        GlobalScope.launch {
            val filter = useCase.getFilters()
            if (filter.isNotEmpty()) {
                filterMap.postValue(filter)
            }

            loadingData.postValue(false)
        }
    }

    fun getPriceByRate(rate: Int, businessType: String) {
        val price = useCase.getPrice(rate, businessType)
        untilPrice.postValue(price.toString())
    }

    fun saveFilter(location:String?, buy: Boolean, rental: Boolean, portal: String?, price: Int) {
        val filters: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
        filters[FilterType.LOCATION] = location ?: ""
        var type = ""
        if (buy) {
            type = BusinessType.SALE.toString()
        }
        else if (rental) {
            type = BusinessType.RENTAL.toString()
        }
        else {
            type = ""
        }
        filters[FilterType.TYPE] = type
        filters[FilterType.PORTAL] = portal ?: ""
        filters[FilterType.PRICE] = price.toString()

        loadingData.postValue(true)
        GlobalScope.launch {
            useCase.saveFilters(filters)
            loadingData.postValue(false)
            finishedSearch.postValue(false)

        }
    }
}