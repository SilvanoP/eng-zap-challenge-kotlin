package br.com.desafio.grupozap.features.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import br.com.desafio.grupozap.R
import br.com.desafio.grupozap.features.search.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragManager: FragmentManager by lazy { supportFragmentManager }
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        currentFragment = SearchFragment.newInstance()

        refreshFragment()
    }

    fun refreshFragment() {
        fragManager.beginTransaction()
            .replace(R.id.fragmentsFrame, currentFragment)
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .addToBackStack(null)
            .commit()
    }

}
