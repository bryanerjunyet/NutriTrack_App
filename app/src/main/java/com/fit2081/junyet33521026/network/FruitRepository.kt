package com.fit2081.junyet33521026.network

import com.fit2081.junyet33521026.data.FruitResponse

/**
 * Centralized Repository for both Fruit Search and Random Fruit Images
 */
class FruitRepository {
    private val fruitApiService = APIService.createFruityViceService()
    private val imageApiService = APIService.createRandomImageService()

    /**
     * Get fruit information by name
     */
    suspend fun getFruitByName(name: String): FruitResponse? {
        return try {
            val response = fruitApiService.getFruitByName(name)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get random fruit image URL
     */
    suspend fun getRandomFruitImageUrl(): String {
        return try {
            val response = imageApiService.getRandomFruitImage()
            if (response.isSuccessful) {
                // Generate unique URL with timestamp to ensure different images
                "https://picsum.photos/400/300?random=${System.currentTimeMillis()}"
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}