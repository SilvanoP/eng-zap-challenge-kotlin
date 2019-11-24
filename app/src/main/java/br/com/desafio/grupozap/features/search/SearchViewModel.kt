package br.com.desafio.grupozap.features.search

import android.util.Log
import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.features.common.FilterView
import br.com.desafio.grupozap.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel (private val useCase: FiltersUseCase): ViewModel(), LifecycleObserver {

    var loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var finishedSearch = MutableLiveData<Boolean>().apply {
        value = false
    }
    var filterView = MutableLiveData<FilterView>().apply {
        value = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun load() {
        loadingData.postValue(true)
        if (filterView.value != null) {

        }
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                useCase.refreshCachedLegalStates()
            }

            filterView.value = useCase.getFilters()
            loadingData.postValue(!result)
        }
    }

    private fun reset() {
        loadingData.value = false
        finishedSearch.value = false
        filterView.value = null
    }

    fun filterForSale(isSale: Boolean) {
        if (filterView.value != null) {
            val newFilter = filterView.value!!
            newFilter.apply {
                forSale = isSale
            }
            filterView.postValue(newFilter)
        }
    }

    fun filterForRent(isRental: Boolean) {
        if (filterView.value != null) {
            val newFilter = filterView.value!!
            newFilter.apply {
                forSale = !isRental
            }
            filterView.postValue(newFilter)
        }
    }

    fun filterByPortal(portal: String) {
        if (filterView.value != null) {
            val newFilter = filterView.value!!
            newFilter.apply {
                this.portal = portal
            }
            filterView.postValue(newFilter)
        }
    }

    fun getPriceByRate(rate: Int, businessType: String) {
        val price = useCase.getPrice(rate, businessType)
        val newFilter = filterView.value!!
        newFilter.apply {
            priceLabel = Utils.fromDoubleToStringTwoDecimal(price.toDouble())
            priceRate = rate
        }
        filterView.postValue(newFilter)
    }

    fun saveFilter(location:String?, buy: Boolean, rental: Boolean, portal: String?, price: Int) {
        loadingData.postValue(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                useCase.saveFilters(location, buy, rental, portal, price)
            }
            finishedSearch.postValue(true)
        }
        loadingData.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        reset()
    }
}