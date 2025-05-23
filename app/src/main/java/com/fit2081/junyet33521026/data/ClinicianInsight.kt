package com.fit2081.junyet33521026.data

/**
 * Data class representing an AI-generated insight
 */
data class ClinicianInsight(
    val title: String,
    val description: String
)

data class FruitNutritions(
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val calories: Int,
    val sugar: Double
)

data class FruitResponse(
    val genus: String,
    val name: String,
    val family: String,
    val order: String,
    val nutritions: FruitNutritions
)

data class FruitErrorResponse(
    val error: String
)