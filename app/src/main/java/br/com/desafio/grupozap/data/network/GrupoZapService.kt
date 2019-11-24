package br.com.desafio.grupozap.data.network

import br.com.desafio.grupozap.data.entities.RealState
import retrofit2.http.GET

interface GrupoZapService {

    @GET("/sources/source-1.json")
    suspend fun getRealStates(): List<RealState>
}