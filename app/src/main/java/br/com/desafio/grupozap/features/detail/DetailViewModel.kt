package br.com.desafio.grupozap.features.detail

import androidx.lifecycle.*
import br.com.desafio.grupozap.domain.RealStateUseCases
import br.com.desafio.grupozap.features.common.RealStateView

class DetailViewModel(val useCases: RealStateUseCases): ViewModel(), LifecycleObserver {

    var realState = MutableLiveData<RealStateView>().apply {
        value = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun getRealState() {
        realState.postValue(useCases.getSelectedRealState())
    }
}