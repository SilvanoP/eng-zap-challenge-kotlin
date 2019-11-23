package br.com.desafio.grupozap.features.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.common.NavigationListener
import br.com.desafio.grupozap.features.common.RealStateView
import kotlinx.android.synthetic.main.fragment_list.view.*


class ListFragment : Fragment(), RealStatesListAdapter.AdapterClickListener {

    private lateinit var viewModel: ListViewModel
    private val realStatesListAdapter: RealStatesListAdapter by lazy {
        RealStatesListAdapter(emptyList<RealStateView>().toMutableList(), this)
    }

    private var realStatesList: MutableList<RealStateView> = ArrayList()
    private var listener: NavigationListener? = null
    private var isLastPage = false
    private var isLoading = false

    private val stateObserver = Observer<RealStatesListState> {
        state ->
        state?.let {
            isLastPage = state.loadedAllItems
            when(state) {
                is DefaultState -> {
                    isLoading = false
                    realStatesListAdapter.addItems(it.realStatesData)
                }
                is LoadingState -> {
                    isLoading = true
                }
                is PaginatingState -> {
                    isLoading = true
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        viewModel.stateLiveData.observe(this, stateObserver)

        initRecycler(view)

        return view
    }

    private fun initRecycler(view: View) {
        val linearLayoutManager = LinearLayoutManager(context)
        view.listRealStatesRecycler.apply {
            layoutManager = linearLayoutManager
            adapter = realStatesListAdapter
            addOnScrollListener(OnScrollListener(linearLayoutManager))
        }
    }

    private fun loadNextPage() {
        viewModel.updateList()
    }

    override fun onItemSelected(realState: RealStateView) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationListener) {
            listener = context
        } else {
            throw RuntimeException("%s must implement OnFragmentInteractionListener".format(context.toString()))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stateLiveData.removeObserver(stateObserver)
    }

    companion object {
        @JvmStatic
        fun newInstance(listViewModel: ListViewModel) =
            ListFragment().apply {
                this.viewModel = listViewModel
            }
    }

    inner class OnScrollListener(val layoutManager: LinearLayoutManager): RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (isLastItemVisible() && !isLoading && !isLastPage) {
                loadNextPage()
            }
        }

        private fun isLastItemVisible(): Boolean {
            val last = layoutManager.findLastVisibleItemPosition()
            return last == (realStatesList.size-1)
        }
    }
}
