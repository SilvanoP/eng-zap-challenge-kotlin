package br.com.desafio.grupozap.features.common

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private var mainFragment: Fragment? = null
    private var sideFragment: Fragment? = null
    private val isTablet: Boolean by lazy {
        resources.getBoolean(R.bool.isTablet)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        savedInstanceState?.let {
            mainFragment = fragManager.getFragment(savedInstanceState, FRAGMENT_MAIN_STATE)
            if (isTablet) {
                sideFragment = fragManager.getFragment(savedInstanceState, FRAGMENT_SIDE_STATE)
            }
        }

        if (isTablet) {
            if (sideFragment == null) sideFragment = initListFragment()
            refreshSideFragment()
        }

        if (mainFragment == null) initMainFragment()
        refreshFragment()
    }

    private fun initMainFragment() {
        val searchViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel::class.java)
        mainFragment = SearchFragment.newInstance(searchViewModel)
    }

    private fun initListFragment(): Fragment {
        val listViewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel::class.java)
        return ListFragment.newInstance(listViewModel)
    }

    private fun refreshSideFragment() {
        if (isTablet && sideFragment == null) {
            initListFragment()
        }

        val fragmentTag = sideFragment!!::class.java.simpleName

        fragManager.beginTransaction()
            .replace(R.id.sideFragmentsFrame, sideFragment!!, fragmentTag)
            .disallowAddToBackStack()
            .commit()
    }

    private fun refreshFragment() {
        invalidateOptionsMenu()
        val fragmentTag = mainFragment!!::class.java.simpleName

        fragManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .replace(R.id.fragmentsFrame, mainFragment!!, fragmentTag)
            .addToBackStack(fragmentTag)
            .commit()
    }

    override fun onSearchEnded() {
        val fragment = initListFragment()
        if(isTablet) {
            sideFragment = fragment
            refreshSideFragment()
        } else {
            mainFragment = fragment
            refreshFragment()
        }
    }

    override fun onRealStateSelected() {
        val detailViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)
        mainFragment = DetailFragment.newInstance(detailViewModel)

        refreshFragment()
    }

    private fun onBackMenuPressed(){
        when(mainFragment?.tag) {
            DetailFragment::class.java.simpleName ->
                if (isTablet) {
                    initMainFragment()
                    refreshFragment()
                } else
                    onSearchEnded()
            ListFragment::class.java.simpleName -> {
                initMainFragment()
                refreshFragment()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val item = menu?.findItem(R.id.menu_back)
        item?.isVisible = (mainFragment !is SearchFragment)

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_back ->
                onBackMenuPressed()
        }
        return true
    }

    override fun onBackPressed() {
        if (fragManager.backStackEntryCount > 1) {
            fragManager.popBackStack()
        } else
            super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        fragManager.putFragment(outState, FRAGMENT_MAIN_STATE, mainFragment!!)
        if (isTablet) {
            fragManager.putFragment(outState, FRAGMENT_SIDE_STATE, sideFragment!!)
        }

        super.onSaveInstanceState(outState)
    }

}
