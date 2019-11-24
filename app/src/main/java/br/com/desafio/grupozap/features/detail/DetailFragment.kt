package br.com.desafio.grupozap.features.detail

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.databinding.FragmentDetailBinding
import br.com.desafio.grupozap.features.common.NavigationListener
import kotlinx.android.synthetic.main.fragment_detail.view.*


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
        subscribeUI(binding)
        val view = binding.root
        initViewPager(view)

        return view
    }

    private fun subscribeUI(binding: FragmentDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        this.lifecycle.addObserver(viewModel)
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
