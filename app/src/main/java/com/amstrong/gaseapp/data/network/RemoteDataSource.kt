package com.amstrong.gaseapp.data.network

import com.amstrong.gaseapp.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RemoteDataSource {
    companion object {
        const val BASE_URL = "http://192.168.100.23:60000/api/"
//        const val BASE_URL = "http://192.168.1.8:8080/api/"

        private val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .serializeNulls()
                .serializeSpecialFloatingPointValues()
                .setLenient()
                .create()
    }

    

    fun <Api> buildApi(
            api: Class<Api>,
            authToken: String? = null
    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                    OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                chain.proceed(chain.request().newBuilder().also {
                                    it.addHeader("Authorization", "Bearer $authToken")
                                }.build())
                            }.also { client ->
                                if (BuildConfig.DEBUG) {
                                    val logging = HttpLoggingInterceptor()
                                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                                    client.addInterceptor(logging)
                                }
                            }.build()
            )
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(api)
    }
}