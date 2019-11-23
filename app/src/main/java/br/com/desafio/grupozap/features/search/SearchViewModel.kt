package br.com.desafio.grupozap.features.search

import android.app.Application
import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.utils.FilterType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel (val useCase: FiltersUseCase, application: Application): AndroidViewModel(application), LifecycleObserver {

    var filterMap = MutableLiveData<Map<FilterType, String>>() .apply {
        value = EnumMap(FilterType::class.java)
    }
    var untilPrice = MutableLiveData<String>().apply {
        value = filterMap.value!![FilterType.PRICE] ?: "0"
    }
    var loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun load() {
        loadingData.value = true
        GlobalScope.launch {
            val result = useCase.refreshCachedLegalStates()
        }

        GlobalScope.launch {
            val filter = useCase.getFilters()
            if (filter.isNotEmpty()) {
                filterMap.value = filter
            }

            loadingData.value = false
        }
    }
}