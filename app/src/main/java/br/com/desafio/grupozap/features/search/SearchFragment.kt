package br.com.desafio.grupozap.features.search

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.desafio.grupozap.databinding.FragmentSearchBinding
import br.com.desafio.grupozap.features.common.NavigationListener

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [NavigationListener] interface
 * to handle interaction events.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    lateinit var viewModel: SearchViewModel
    private var listener: NavigationListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        return binding.root
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SearchFragment.
         */
        @JvmStatic
        fun newInstance(searchViewModel: SearchViewModel): SearchFragment {
            val fragment = SearchFragment()
            fragment.viewModel = searchViewModel
            return fragment
        }
    }
}
