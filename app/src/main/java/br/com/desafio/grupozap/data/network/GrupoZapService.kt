package br.com.desafio.grupozap.data.network

import br.com.desafio.grupozap.data.entities.remote.RealStateResponse
import io.reactivex.Single
import retrofit2.http.GET

interface GrupoZapService {

    @GET("/sources/source-1.json")
    fun getRealStates(): Single<RealStateResponse>
}