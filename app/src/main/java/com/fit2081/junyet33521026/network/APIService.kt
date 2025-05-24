package com.fit2081.junyet33521026.network

import com.fit2081.junyet33521026.data.FruitResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * Centralised API service interface for all external API calls
 */
interface APIService {

    /**
     * Endpoint to fetch fruit information by name.
     * @param name The name of the fruit to search for.
     * @return A [Response] containing a [FruitResponse] object with the fruit details.
     */
    @GET("api/fruit/{name}")
    suspend fun getFruitByName(@Path("name") name: String): Response<FruitResponse>

    @GET("400/300")
    /**
     * Endpoint to fetch a random image.
     * @return A [Response] containing the image as a [ResponseBody].
     */
    suspend fun getRandomImage(): Response<ResponseBody>

    /**
     * Companion object to create instances of the APIService
     * that provides methods to create services for different APIs.
     */
    companion object {
        // base URLs for different APIs
        private const val FRUITYVICE_BASE_URL = "https://www.fruityvice.com/"
        private const val PICSUM_BASE_URL = "https://picsum.photos/"

        /**
         * Creates an instance of APIService for FruityVice API.
         * @return An instance of [APIService] configured for FruityVice.
         */
        fun createFruityViceService(): APIService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FRUITYVICE_BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }

        /**
         * Creates an instance of APIService for Picsum API to fetch random images.
         * @return An instance of [APIService] configured for Picsum.
         */
        fun createRandomImageService(): APIService {
            val retrofit = Retrofit.Builder()
                .baseUrl(PICSUM_BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)
        }
    }
}