package br.com.desafio.grupozap.features.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.databinding.FragmentSearchBinding
import br.com.desafio.grupozap.features.common.NavigationListener
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.PortalType
import br.com.desafio.grupozap.utils.Utils
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment() {

    lateinit var viewModel: SearchViewModel
    private var listener: NavigationListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        subscribeUI(binding)
        val view = binding.root

        loadListeners(view)

        return view
    }

    private fun subscribeUI(binding: FragmentSearchBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        this.lifecycle.addObserver(viewModel)
        viewModel.finishedSearch.observe(binding.lifecycleOwner!!, androidx.lifecycle.Observer {
            if (it) {
                listener?.onSearchEnded()
            }
        })
        viewModel.loadingData.observe(binding.lifecycleOwner!!, Observer {
            searchProgressBar.apply {
                visibility = if (it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun loadListeners(view: View) {
        view.searchBuyToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.filterForSale(isChecked)
        }

        view.searchRentToggleButton.setOnCheckedChangeListener { _, isChecked ->
            viewModel.filterForRent(isChecked)
        }

        view.searchPortalRadioGroup.setOnCheckedChangeListener { _, radioButtonId ->
            when(radioButtonId) {
                view.searchNoFilterRadioButton.id ->
                    viewModel.filterByPortal(PortalType.ALL.toString())
                view.searchZapRadioButton.id ->
                    viewModel.filterByPortal(PortalType.ZAP.toString())
                view.searchVivaRealRadioButton.id ->
                    viewModel.filterByPortal(PortalType.VIVA_REAL.toString())
            }
        }

        view.searchPriceSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var businessType = ""
                if (searchBuyToggleButton.isChecked) {
                    businessType = BusinessType.SALE.toString()
                } else if (searchRentToggleButton.isChecked) {
                    businessType = BusinessType.RENTAL.toString()
                }
                viewModel.getPriceByRate(progress, businessType)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        view.searchFilterButton.setOnClickListener {
            saveFilter()
        }
    }

    private fun saveFilter() {
        val location: String? = searchCityNeighborhoodAutoComplete.text.toString()
        val buy = searchBuyToggleButton.isChecked
        val rental = searchRentToggleButton.isChecked
        val portal = when(searchPortalRadioGroup.checkedRadioButtonId) {
            searchZapRadioButton.id -> PortalType.ZAP.toString()
            searchVivaRealRadioButton.id -> PortalType.VIVA_REAL.toString()
            searchNoFilterRadioButton.id -> PortalType.ALL.toString()
            else -> ""
        }
        val price = searchPriceSeekBar.progress
        viewModel.saveFilter(location, buy, rental, portal, price)
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

    companion object {
        @JvmStatic
        fun newInstance(searchViewModel: SearchViewModel) = SearchFragment().apply {
            this.viewModel = searchViewModel
        }
    }
}
