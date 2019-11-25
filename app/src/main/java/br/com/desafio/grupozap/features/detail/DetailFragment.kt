package br.com.desafio.grupozap.features.detail

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.databinding.FragmentDetailBinding
import br.com.desafio.grupozap.features.common.NavigationListener
import br.com.desafio.grupozap.utils.BusinessType
import br.com.desafio.grupozap.utils.Utils
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import java.lang.StringBuilder
import java.util.*


class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private val imageAdapter: RealStateViewPagerAdapter by lazy {
        RealStateViewPagerAdapter(emptyList())
    }
    private var listener: NavigationListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        initViewPager(view)
        subscribeUI(binding)

        return view
    }

    private fun subscribeUI(binding: FragmentDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        this.lifecycle.addObserver(viewModel)
        viewModel.realState.observe(binding.lifecycleOwner!!, Observer {
            if (it != null) {
                imageAdapter.swapImages(it.images)
                val address =
                    if (it.city.isNullOrEmpty() || it.neighborhood.isNullOrEmpty()) getAddressFromGeocode(
                        it.lat,
                        it.lon
                    )
                    else "%s - %s".format(it.neighborhood, it.city)
                detailAddressText.text = address
                val fullPrice =
                    if (it.businessType == BusinessType.SALE.toString()) it.price else it.rentalTotalPrice

                detailPriceText.text = getString(R.string.price_full).format(Utils.fromDoubleToStringTwoDecimal(fullPrice.toDouble()))
                detailBedroomText.text = getString(R.string.bedrooms).format(it.bedrooms)
                detailAreaText.text = getString(R.string.usable_area).format(it.usableAreas)
                detailCondoFeeText.text = getString(R.string.monthly_condo_fee)
                    .format(Utils.fromDoubleToStringTwoDecimal(it.monthlyCondoFee.toDouble()))
                detailYearlyIPTUText.text = getString(R.string.yearly_iptu)
                    .format(Utils.fromDoubleToStringTwoDecimal(it.yearlyIptu.toDouble()))
                detailParkingSpacesText.text = getString(R.string.parking_spaces).format(it.parkingSpaces)
            }
        })
    }

    private fun getAddressFromGeocode(lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(lat, lon, 1)[0]
        val result = StringBuilder().apply {
            var index = 0
            var line: String? = ""
            while(line!=null) {
                line = address.getAddressLine(index)
                index++
                append(line?:"")
                append(" ")
            }
        }
        return result.toString()
    }

    private fun initViewPager(view: View) {
        view.detailImageViewPager.adapter = imageAdapter
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
        fun newInstance(detailViewModel: DetailViewModel) =
            DetailFragment().apply {
                viewModel = detailViewModel
            }
    }
}
