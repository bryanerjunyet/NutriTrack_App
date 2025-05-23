package com.fit2081.junyet33521026.data

data class FruitInsight(
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
    val nutritions: FruitInsight
)