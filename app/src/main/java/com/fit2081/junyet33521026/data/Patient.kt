package com.fit2081.junyet33521026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient {
    @PrimaryKey
    val userId: String,
    val phoneNumber: String,
    var name: String = "",
    var password: String = "",
    val sex: String,

    // Total scores
    val heifaTotalScoreMale: Float,
    val heifaTotalScoreFemale: Float,

    // Discretionary
    val discretionaryHeifaScoreMale: Float,
    val discretionaryHeifaScoreFemale: Float,
    val discretionaryServeSize: Float,

    // Vegetables
    val vegetablesHeifaScoreMale: Float,
    val vegetablesHeifaScoreFemale: Float,
    val vegetablesWithLegumesAllocatedServeSize: Float,
    val legumesAllocatedVegetables: Float,
    val vegetablesVariationsScore: Float,
    val vegetablesCruciferous: Float,
    val vegetablesTuberAndBulb: Float,
    val vegetablesOther: Float,
    val legumes: Float,
    val vegetablesGreen: Float,
    val vegetablesRedAndOrange: Float,

    // Fruits
    val fruitHeifaScoreMale: Float,
    val fruitHeifaScoreFemale: Float,
    val fruitServeSize: Float,
    val fruitVariationsScore: Float,
    val fruitPome: Float,
    val fruitTropicalAndSubtropical: Float,
    val fruitBerry: Float,
    val fruitStone: Float,
    val fruitCitrus: Float,
    val fruitOther: Float,

    // Grains and cereals
    val grainsAndCerealsHeifaScoreMale: Float,
    val grainsAndCerealsHeifaScoreFemale: Float,
    val grainsAndCerealsServeSize: Float,
    val grainsAndCerealsNonWholegrains: Float,

    // Whole grains
    val wholegrainsHeifaScoreMale: Float,
    val wholegrainsHeifaScoreFemale: Float,
    val wholegrainsServeSize: Float,

    // Meat and alternatives
    val meatAndAlternativesHeifaScoreMale: Float,
    val meatAndAlternativesHeifaScoreFemale: Float,
    val meatAndAlternativesWithLegumesAllocatedServeSize: Float,
    val legumesAllocatedMeatAndAlternatives: Float,

    // Dairy and alternatives
    val dairyAndAlternativesHeifaScoreMale: Float,
    val dairyAndAlternativesHeifaScoreFemale: Float,
    val dairyAndAlternativesServeSize: Float,

    // Sodium
    val sodiumHeifaScoreMale: Float,
    val sodiumHeifaScoreFemale: Float,
    val sodiumMgMilligrams: Float,

    // Alcohol
    val alcoholHeifaScoreMale: Float,
    val alcoholHeifaScoreFemale: Float,
    val alcoholStandardDrinks: Float,

    // Water
    val waterHeifaScoreMale: Float,
    val waterHeifaScoreFemale: Float,
    val water: Float,
    val waterTotalMl: Float,
    val beverageTotalMl: Float,

    // Sugar
    val sugarHeifaScoreMale: Float,
    val sugarHeifaScoreFemale: Float,
    val sugar: Float,

    // Fats
    val saturatedFatHeifaScoreMale: Float,
    val saturatedFatHeifaScoreFemale: Float,
    val saturatedFat: Float,
    val unsaturatedFatHeifaScoreMale: Float,
    val unsaturatedFatHeifaScoreFemale: Float,
    val unsaturatedFatServeSize: Float
}