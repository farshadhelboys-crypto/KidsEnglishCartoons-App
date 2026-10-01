package com.kidsenglish.cartoons.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("cartoons")
    suspend fun getCartoons(): CartoonsResponse

    companion object {
        // User's Cloudflare Worker
        private const val BASE_URL = "https://acrtoonfarinaz.farshadhelboys.workers.dev/"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
