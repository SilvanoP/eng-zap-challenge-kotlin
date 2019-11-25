package br.com.desafio.grupozap.features.list

import android.util.Log
import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.ListRealStatesUseCases
import br.com.desafio.grupozap.features.common.RealStateView
import br.com.desafio.grupozap.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListViewModel (val useCase: ListRealStatesUseCases): ViewModel(), LifecycleObserver {

    var stateLiveData = MutableLiveData<RealStatesListState>().apply {
        value = DefaultState(0, false, emptyList())
    }
    var loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }
    var selectedRealState = MutableLiveData<Boolean>().apply {
        value = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun updateList() {
        val page = getCurrentPage()
        stateLiveData.value = if (page == 0)
            LoadingState(0, false, getCurrentRealStates())
        else
            PaginatingState(page, false, getCurrentRealStates())

        Log.d("VIEW MODEL", "PAGE %d".format(page))
        getRealStatesList(page)
    }

    fun restoreList() {
        val page = getCurrentPage()
        stateLiveData.value = DefaultState(page, false, getCurrentRealStates())
    }

    fun realStateSelected(index: Int) {
        val result = useCase.selectRealState(index)
        selectedRealState.postValue(result)
    }

    fun reset() {
        stateLiveData.postValue(DefaultState(0, false, emptyList()))
        loadingData.postValue(false)
        selectedRealState.postValue(false)
    }

    private fun getRealStatesList(page: Int) {
        loadingData.postValue(true)
        Log.d("VIEW MODEL", "LOADING DATA")
        viewModelScope.launch {
            val realStatesList = withContext(Dispatchers.IO) {
                useCase.getNextPage(page)
            }

            onRealStatesListReceived(realStatesList)
        }
    }

    private fun onRealStatesListReceived(realStatesList: List<RealStateView>) {
        val currentList = getCurrentRealStates().toMutableList()
        val currentPage = getCurrentPage() +1
        val areAllItemsLoaded = realStatesList.size < Constants.PAGE_SIZE

        currentList.addAll(realStatesList)
        loadingData.postValue(false)
        Log.d("VIEW MODEL", "DEFAULT END? %s".format(areAllItemsLoaded))
        stateLiveData.value = DefaultState(currentPage, areAllItemsLoaded, currentList)
    }

    private fun getCurrentPage() = stateLiveData.value?.page ?: 0
    private fun getCurrentRealStates() = stateLiveData.value?.realStatesData ?: emptyList()
}