package br.com.desafio.grupozap.features.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationListener {

    companion object {
        private const val FRAGMENT_STATE = "fragment"
    }

    private val fragManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        if (savedInstanceState?.isEmpty == false) {
            currentFragment = fragManager.getFragment(savedInstanceState, FRAGMENT_STATE)!!
        } else {
            currentFragment = SearchFragment.newInstance()
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
