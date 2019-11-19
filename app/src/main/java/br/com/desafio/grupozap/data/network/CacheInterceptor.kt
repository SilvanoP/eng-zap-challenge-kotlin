package br.com.desafio.grupozap.data.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class CacheInterceptor: Interceptor {

    var isOnline: Boolean = false

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val headerValue = if (isOnline) {
            String.format("public, max-age=%d", 5)
        } else {
            String.format("public, only-if-cached, max-stale=%d", 60 * 60 * 24 * 7)
        }

        val newRequest: Request = request.newBuilder()
            .header("Cache-Control", headerValue)
            .build()

        return chain.proceed(newRequest)
    }
}