package br.com.desafio.grupozap.data

import android.content.Context
import android.net.ConnectivityManager
import br.com.desafio.grupozap.data.entities.RealState
import br.com.desafio.grupozap.data.network.GrupoZapService

class DataRepository(val context: Context, val service: GrupoZapService) {

    fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo.isConnected
    }

    fun getAllRealStates(): List<RealState> {
        // TODO implement
        return arrayListOf()
    }
}