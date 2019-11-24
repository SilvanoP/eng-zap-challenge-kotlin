package br.com.desafio.grupozap.features.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.desafio.grupozap.domain.RealStateUseCases
import br.com.desafio.grupozap.features.common.RealStateView
import br.com.desafio.grupozap.utils.Constants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ListViewModel (val useCase: RealStateUseCases): ViewModel(), LifecycleObserver {

    var stateLiveData = MutableLiveData<RealStatesListState>().apply {
        value = DefaultState(0, false, emptyList())
    }
    var loadingData = MutableLiveData<Boolean>().apply {
        value = false
    }

    fun updateList() {
        val page = getCurrentPage()
        stateLiveData.value = if (page == 0)
            LoadingState(0, false, getCurrentRealStates())
        else
            PaginatingState(page, false, getCurrentRealStates())

        getRealStatesList(page)
    }

    fun restoreList() {
        val page = getCurrentPage()
        stateLiveData.value = DefaultState(page, false, getCurrentRealStates())
    }

    private fun getRealStatesList(page: Int) {
        loadingData.postValue(true)
        GlobalScope.launch {
            val realStatesList = useCase.getNextPage(page)
            onRealStatesListReceived(realStatesList)
        }
    }

    private fun onRealStatesListReceived(realStatesList: List<RealStateView>) {
        val currentList = getCurrentRealStates().toMutableList()
        val currentPage = getCurrentPage() +1
        val areAllItemsLoaded = realStatesList.size < Constants.PAGE_SIZE

        currentList.addAll(realStatesList)
        loadingData.postValue(false)
        stateLiveData.value = DefaultState(currentPage, areAllItemsLoaded, currentList)
    }

    private fun getCurrentPage() = stateLiveData.value?.page ?: 0
    private fun getCurrentRealStates() = stateLiveData.value?.realStatesData ?: emptyList()
}