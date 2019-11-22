package br.com.desafio.grupozap.features.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import br.com.desafio.grupozap.domain.FiltersUseCase
import br.com.desafio.grupozap.utils.FilterType
import java.util.*

class SearchViewModel (useCase: FiltersUseCase, application: Application): AndroidViewModel(application) {

    var filterMap: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
}