package xyz.larkzhh.echomusic.http

import retrofit2.Retrofit


class RetrofitClient constructor(private val builder: Retrofit.Builder) {

    private val retrofitCache = hashMapOf<String, Retrofit>()

    fun setBaseUrl(baseUrl: String): Retrofit {
        val cacheRetrofit = retrofitCache[baseUrl]
        return if (cacheRetrofit != null) {
            cacheRetrofit
        } else {
            val retrofit = builder
                .baseUrl(baseUrl)
                .build()
            retrofitCache[baseUrl] = retrofit
            retrofit
        }
    }

}