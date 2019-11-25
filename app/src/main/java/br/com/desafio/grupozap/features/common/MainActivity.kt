package br.com.desafio.grupozap.features.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.detail.DetailFragment
import br.com.desafio.grupozap.features.detail.DetailViewModel
import br.com.desafio.grupozap.features.list.ListFragment
import br.com.desafio.grupozap.features.list.ListViewModel
import br.com.desafio.grupozap.features.search.SearchFragment
import br.com.desafio.grupozap.features.search.SearchViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val FRAGMENT_SIDE_STATE = "side_fragment"
private const val FRAGMENT_MAIN_STATE = "main_fragment"

class MainActivity : AppCompatActivity(), NavigationListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val fragManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var mainFragment: Fragment
    private var sideFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (isTablet()) {
            refreshSideFragment()
        }

        val searchViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
        mainFragment = SearchFragment.newInstance(searchViewModel)

        refreshFragment()
    }

    private fun refreshSideFragment() {
        if (isTablet() && sideFragment == null) {
            val listViewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel::class.java)
            sideFragment = ListFragment.newInstance(listViewModel)
        }

        fragManager.beginTransaction()
            .replace(R.id.sideFragmentsFrame, sideFragment!!)
            .disallowAddToBackStack()
            .commit()
    }

    private fun refreshFragment() {
        fragManager.beginTransaction()
            .replace(R.id.fragmentsFrame, mainFragment)
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .addToBackStack(null)
            .commit()
    }

    override fun onSearchEnded() {
        val listViewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel::class.java)

        if(isTablet()) {
            sideFragment = ListFragment.newInstance(listViewModel)
            refreshSideFragment()
        } else {
            mainFragment = ListFragment.newInstance(listViewModel)
            refreshFragment()
        }
    }

    override fun onRealStateSelected() {
        val detailViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)
        mainFragment = DetailFragment.newInstance(detailViewModel)

        refreshFragment()
    }

    fun isTablet(): Boolean = resources.getBoolean(R.bool.isTablet)

    override fun onBackPressed() {
        if (fragManager.backStackEntryCount > 0) {
            fragManager.popBackStack()
        } else
            super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragManager.putFragment(outState, FRAGMENT_MAIN_STATE, mainFragment)
        if (isTablet()) {
            fragManager.putFragment(outState, FRAGMENT_SIDE_STATE, sideFragment!!)
        }

        super.onSaveInstanceState(outState)
    }

}
