package br.com.desafio.grupozap.data

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.data.network.CacheInterceptor
import br.com.desafio.grupozap.data.network.GrupoZapService
import br.com.desafio.grupozap.domain.DataRepository
import br.com.desafio.grupozap.utils.FilterType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositoryImpl @Inject constructor(private val context: Context, private val service: GrupoZapService,
                                             private val interceptor: CacheInterceptor,
                                             private val preferences: SharedPreferences): DataRepository {

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo.isConnected
    }

    override suspend fun getAllRealStates(): List<RealState> {
        interceptor.isOnline = isOnline()

        return service.getRealStates()
    }

    override suspend fun getFilter(filter: String): String? {
        return preferences.getString(filter, "")
    }

    override suspend fun saveFilter(filter: String, value: String): Boolean {
        return preferences.edit().putString(filter, value).commit()
    }
}