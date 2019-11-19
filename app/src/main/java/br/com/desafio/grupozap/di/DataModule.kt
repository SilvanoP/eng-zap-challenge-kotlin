package br.com.desafio.grupozap.di

import android.content.Context
import br.com.desafio.grupozap.data.DataRepositoryImpl
import br.com.desafio.grupozap.data.network.CacheInterceptor
import br.com.desafio.grupozap.data.network.GrupoZapService
import br.com.desafio.grupozap.domain.DataRepository
import br.com.desafio.grupozap.utils.Constants
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object DataModule {

    @Provides
    @Singleton
    @JvmStatic
    fun providesCacheInterceptor(): CacheInterceptor {
        val cacheInterceptor = CacheInterceptor()
        cacheInterceptor.isOnline = true

        return cacheInterceptor
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesOkHttpClient(context: Context, interceptor: CacheInterceptor): OkHttpClient {
        val cacheSize: Long = 5*1024*1024 //5Mb
        val cache = Cache(context.cacheDir, cacheSize)
        return OkHttpClient.Builder().cache(cache).addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesGrupoZapService(client: OkHttpClient): GrupoZapService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
            .create(GrupoZapService::class.java)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun providesDataRepository(dataRepositoryImpl: DataRepositoryImpl): DataRepository {
        return dataRepositoryImpl
    }
}