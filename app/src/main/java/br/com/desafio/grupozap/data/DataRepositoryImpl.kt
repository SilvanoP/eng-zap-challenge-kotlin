package br.com.desafio.grupozap.data

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.data.network.CacheInterceptor
import br.com.desafio.grupozap.data.network.GrupoZapService
import br.com.desafio.grupozap.domain.DataRepository
import br.com.desafio.grupozap.utils.FilterType
import br.com.desafio.grupozap.utils.PortalType
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

        return service.getRealStates().realStates
    }

    override suspend fun getFilters(): Map<FilterType, String> {
        val filterMap: MutableMap<FilterType, String> = EnumMap(FilterType::class.java)
        FilterType.values().forEach {
            filterMap[it] = preferences.getString(it.toString(),"")!!
        }

        return filterMap.toMap()
    }

    override suspend fun saveFilters(filterMap: Map<FilterType, String>) {
        with(preferences.edit()) {
            filterMap.keys.forEach { key ->
                putString(key.toString(), filterMap[key])
            }
            commit()
        }
    }
}