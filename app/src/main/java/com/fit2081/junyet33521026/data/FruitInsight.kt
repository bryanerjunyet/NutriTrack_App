package com.fit2081.junyet33521026.data

/**
 * Data class for the nutritional insights of a fruit searched.
 */
data class FruitInsight(
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val calories: Int,
    val sugar: Double
)

/**
 * Data class for the response of a fruit search API.
 */
data class FruitResponse(
    val genus: String,
    val name: String,
    val family: String,
    val order: String,
    val nutritions: FruitInsight
)