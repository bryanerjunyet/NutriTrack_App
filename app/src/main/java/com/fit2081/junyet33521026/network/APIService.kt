package com.fit2081.junyet33521026.network

import com.fit2081.junyet33521026.data.FruitResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Centralized API Service Interface for all external API calls
 */
interface APIService {
    // FruityVice API endpoint
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Response<FruitResponse>

    // Random fruit image endpoint
    @GET("400/300")
    suspend fun getRandomFruitImage(): Response<okhttp3.ResponseBody>

    companion object {
        private const val FRUITYVICE_BASE_URL = "https://www.fruityvice.com/"
        private const val PICSUM_BASE_URL = "https://picsum.photos/"

        fun createFruityViceService(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FRUITYVICE_BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }

        fun createRandomImageService(): APIService {
            val retrofit = Retrofit.Builder()
                .baseUrl(PICSUM_BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}