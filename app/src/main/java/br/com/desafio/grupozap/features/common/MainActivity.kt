package br.com.desafio.grupozap.features.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.search.SearchFragment
import br.com.desafio.grupozap.features.search.SearchViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val FRAGMENT_STATE = "fragment"

class MainActivity : AppCompatActivity(), NavigationListener {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val fragManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        currentFragment = if (savedInstanceState?.isEmpty == false) {
            fragManager.getFragment(savedInstanceState, FRAGMENT_STATE)!!
        } else {
            val searchViewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
            SearchFragment.newInstance(searchViewModel)
        }

        refreshFragment()
    }

    private fun refreshFragment() {
        fragManager.beginTransaction()
            .replace(R.id.fragmentsFrame, currentFragment)
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .addToBackStack(null)
            .commit()
    }

    override fun onSearchEnded() {

    }

    override fun onRealStateSelected() {

    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragManager.putFragment(outState, FRAGMENT_STATE, currentFragment)

        super.onSaveInstanceState(outState)
    }

}
