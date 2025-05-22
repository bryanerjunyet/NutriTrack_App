package com.fit2081.junyet33521026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey val userId: String,
    val phoneNumber: String,
    var name: String ?= null,
    var password: String ?= null,
    val sex: String,

    // Total scores
    val heifaTotalScore: Float,

    // Discretionary
    val discretionaryHeifaScore: Float,

    // Vegetables
    val vegetablesHeifaScore: Float,

    // Fruits
    val fruitHeifaScore: Float,
    val fruitSizeScore: Float,
    val fruitVariationsScore: Float,

    // Grains and cereals
    val grainsAndCerealsHeifaScore: Float,

    // Whole grains
    val wholegrainsHeifaScore: Float,

    // Meat and alternatives
    val meatAndAlternativesHeifaScore: Float,

    // Dairy and alternatives
    val dairyAndAlternativesHeifaScore: Float,

    // Sodium
    val sodiumHeifaScore: Float,

    // Alcohol
    val alcoholHeifaScore: Float,

    // Water
    val waterHeifaScore: Float,

    // Sugar
    val sugarHeifaScore: Float,

    // Fats
    val saturatedFatHeifaScore: Float,
    val unsaturatedFatHeifaScore: Float

)