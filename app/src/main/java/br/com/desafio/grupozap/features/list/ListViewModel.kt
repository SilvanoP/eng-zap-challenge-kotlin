package br.com.desafio.grupozap.features.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import br.com.desafio.grupozap.domain.RealStateUseCases

class ListViewModel (val useCase: RealStateUseCases, application: Application): AndroidViewModel(application),
    LifecycleObserver {
}