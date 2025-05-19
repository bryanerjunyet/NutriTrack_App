package com.fit2081.junyet33521026.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.io.BufferedReader
import java.io.InputStreamReader

class PatientRepository(context: Context) {
    private val patientDao: PatientDao

    init {
        patientDao = NutriTrackDatabase.getDatabase(context).patientDao()
    }

    suspend fun getPatient(userId: String): Patient? = patientDao.getPatient(userId)

    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    fun getAllUserIds(): Flow<List<String>> = patientDao.getAllUserIds()

    suspend fun updatePatient(patient: Patient) = patientDao.update(patient)

    suspend fun importPatientsFromCsv(context: Context) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean("csv_imported", false)) {
            val patients = parseCsv(context)
            patients.forEach { patientDao.insert(it) }
            sharedPref.edit().putBoolean("csv_imported", true).apply()
        }
    }

    private fun parseCsv(context: Context): List<Patient> {
        val patients = mutableListOf<Patient>()
        val assets = context.assets

        try {
            val inputStream = assets.open("nutritrack_users.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // Read header row to map column names
            val headerRow = reader.readLine() ?: return emptyList()
            val headers = headerRow.split(",").map { it.trim() }
            val headerMap = headers.mapIndexed { index, header -> header to index }.toMap()

            reader.useLines { lines ->
                lines.forEach { line ->
                    val values = line.split(",").map { it.trim() }

                    val patient = Patient(
                        userId = values.getOrNull(headerMap["User_ID"] ?: -1) ?: "",
                        phoneNumber = values.getOrNull(headerMap["PhoneNumber"] ?: -1) ?: "",
                        sex = values.getOrNull(headerMap["Sex"] ?: -1) ?: "",

                        // Total scores
                        heifaTotalScoreMale = values.getOrNull(
                            headerMap["HEIFAtotalscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        heifaTotalScoreFemale = values.getOrNull(
                            headerMap["HEIFAtotalscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Discretionary
                        discretionaryHeifaScoreMale = values.getOrNull(
                            headerMap["DiscretionaryHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        discretionaryHeifaScoreFemale = values.getOrNull(
                            headerMap["DiscretionaryHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        discretionaryServeSize = values.getOrNull(
                            headerMap["Discretionaryservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Vegetables
                        vegetablesHeifaScoreMale = values.getOrNull(
                            headerMap["VegetablesHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesHeifaScoreFemale = values.getOrNull(
                            headerMap["VegetablesHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesWithLegumesAllocatedServeSize = values.getOrNull(
                            headerMap["Vegetableswithlegumesallocatedservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        legumesAllocatedVegetables = values.getOrNull(
                            headerMap["LegumesallocatedVegetables"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesVariationsScore = values.getOrNull(
                            headerMap["Vegetablesvariationsscore"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesCruciferous = values.getOrNull(
                            headerMap["VegetablesCruciferous"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesTuberAndBulb = values.getOrNull(
                            headerMap["VegetablesTuberandbulb"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        vegetablesOther = values.getOrNull(headerMap["VegetablesOther"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        legumes = values.getOrNull(headerMap["Legumes"] ?: -1)?.toFloatOrNull()
                            ?: 0f,
                        vegetablesGreen = values.getOrNull(headerMap["VegetablesGreen"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        vegetablesRedAndOrange = values.getOrNull(
                            headerMap["VegetablesRedandorange"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Fruits
                        fruitHeifaScoreMale = values.getOrNull(
                            headerMap["FruitHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        fruitHeifaScoreFemale = values.getOrNull(
                            headerMap["FruitHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        fruitServeSize = values.getOrNull(headerMap["Fruitservesize"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        fruitVariationsScore = values.getOrNull(
                            headerMap["Fruitvariationsscore"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        fruitPome = values.getOrNull(headerMap["FruitPome"] ?: -1)?.toFloatOrNull()
                            ?: 0f,
                        fruitTropicalAndSubtropical = values.getOrNull(
                            headerMap["FruitTropicalandsubtropical"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        fruitBerry = values.getOrNull(headerMap["FruitBerry"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        fruitStone = values.getOrNull(headerMap["FruitStone"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        fruitCitrus = values.getOrNull(headerMap["FruitCitrus"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        fruitOther = values.getOrNull(headerMap["FruitOther"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,

                        // Grains and cereals
                        grainsAndCerealsHeifaScoreMale = values.getOrNull(
                            headerMap["GrainsandcerealsHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        grainsAndCerealsHeifaScoreFemale = values.getOrNull(
                            headerMap["GrainsandcerealsHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        grainsAndCerealsServeSize = values.getOrNull(
                            headerMap["Grainsandcerealsservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        grainsAndCerealsNonWholegrains = values.getOrNull(
                            headerMap["GrainsandcerealsNonwholegrains"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Whole grains
                        wholegrainsHeifaScoreMale = values.getOrNull(
                            headerMap["WholegrainsHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        wholegrainsHeifaScoreFemale = values.getOrNull(
                            headerMap["WholegrainsHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        wholegrainsServeSize = values.getOrNull(
                            headerMap["Wholegrainsservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Meat and alternatives
                        meatAndAlternativesHeifaScoreMale = values.getOrNull(
                            headerMap["MeatandalternativesHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        meatAndAlternativesHeifaScoreFemale = values.getOrNull(
                            headerMap["MeatandalternativesHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        meatAndAlternativesWithLegumesAllocatedServeSize = values.getOrNull(
                            headerMap["Meatandalternativeswithlegumesallocatedservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        legumesAllocatedMeatAndAlternatives = values.getOrNull(
                            headerMap["LegumesallocatedMeatandalternatives"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Dairy and alternatives
                        dairyAndAlternativesHeifaScoreMale = values.getOrNull(
                            headerMap["DairyandalternativesHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        dairyAndAlternativesHeifaScoreFemale = values.getOrNull(
                            headerMap["DairyandalternativesHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        dairyAndAlternativesServeSize = values.getOrNull(
                            headerMap["Dairyandalternativesservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Sodium
                        sodiumHeifaScoreMale = values.getOrNull(
                            headerMap["SodiumHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        sodiumHeifaScoreFemale = values.getOrNull(
                            headerMap["SodiumHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        sodiumMgMilligrams = values.getOrNull(headerMap["Sodiummgmilligrams"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,

                        // Alcohol
                        alcoholHeifaScoreMale = values.getOrNull(
                            headerMap["AlcoholHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        alcoholHeifaScoreFemale = values.getOrNull(
                            headerMap["AlcoholHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        alcoholStandardDrinks = values.getOrNull(
                            headerMap["Alcoholstandarddrinks"] ?: -1
                        )?.toFloatOrNull() ?: 0f,

                        // Water
                        waterHeifaScoreMale = values.getOrNull(
                            headerMap["WaterHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        waterHeifaScoreFemale = values.getOrNull(
                            headerMap["WaterHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        water = values.getOrNull(headerMap["Water"] ?: -1)?.toFloatOrNull() ?: 0f,
                        waterTotalMl = values.getOrNull(headerMap["WaterTotalmL"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        beverageTotalMl = values.getOrNull(headerMap["BeverageTotalmL"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,

                        // Sugar
                        sugarHeifaScoreMale = values.getOrNull(
                            headerMap["SugarHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        sugarHeifaScoreFemale = values.getOrNull(
                            headerMap["SugarHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        sugar = values.getOrNull(headerMap["Sugar"] ?: -1)?.toFloatOrNull() ?: 0f,

                        // Fats
                        saturatedFatHeifaScoreMale = values.getOrNull(
                            headerMap["SaturatedFatHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        saturatedFatHeifaScoreFemale = values.getOrNull(
                            headerMap["SaturatedFatHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        saturatedFat = values.getOrNull(headerMap["SaturatedFat"] ?: -1)
                            ?.toFloatOrNull() ?: 0f,
                        unsaturatedFatHeifaScoreMale = values.getOrNull(
                            headerMap["UnsaturatedFatHEIFAscoreMale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        unsaturatedFatHeifaScoreFemale = values.getOrNull(
                            headerMap["UnsaturatedFatHEIFAscoreFemale"] ?: -1
                        )?.toFloatOrNull() ?: 0f,
                        unsaturatedFatServeSize = values.getOrNull(
                            headerMap["UnsaturatedFatservesize"] ?: -1
                        )?.toFloatOrNull() ?: 0f
                    )

                    patients.add(patient)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return patients
    }

}