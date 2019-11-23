package br.com.desafio.grupozap.features.list

import br.com.desafio.grupozap.features.common.RealStateView

sealed class RealStatesListState {
    abstract val page: Int
    abstract val loadedAllItems: Boolean
    abstract val realStatesData: List<RealStateView>
}

data class DefaultState(override val page: Int, override val loadedAllItems: Boolean,
                        override val realStatesData: List<RealStateView>): RealStatesListState()
data class LoadingState(override val page: Int, override val loadedAllItems: Boolean,
                        override val realStatesData: List<RealStateView>): RealStatesListState()
data class PaginatingState(override val page: Int, override val loadedAllItems: Boolean,
                        override val realStatesData: List<RealStateView>): RealStatesListState()