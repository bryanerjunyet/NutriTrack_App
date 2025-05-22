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

    suspend fun getPatient(userId: String): Patient = patientDao.getPatient(userId)

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
            val headers = headerRow.replace("\uFEFF", "").split(",").map { it.trim() }
            val headerMap = headers.mapIndexed { index, header -> header to index }.toMap()

            reader.useLines { lines ->
                lines.forEach { line ->
                    val values = line.split(",").map { it.trim() }

                    val sex = values.getOrNull(headerMap["Sex"] ?: -1) ?: ""
                    val patient: Patient

                    if (sex.equals("Male", ignoreCase = true)) {
                        patient = Patient(
                            userId = values.getOrNull(headerMap["User_ID"] ?: -1) ?: "",
                            phoneNumber = values.getOrNull(headerMap["PhoneNumber"] ?: -1) ?: "",
                            sex = sex,
                            heifaTotalScore = values.getOrNull(headerMap["HEIFAtotalscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            discretionaryHeifaScore = values.getOrNull(headerMap["DiscretionaryHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            vegetablesHeifaScore = values.getOrNull(headerMap["VegetablesHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitHeifaScore = values.getOrNull(headerMap["FruitHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitSizeScore = values.getOrNull(headerMap["Fruitservesize"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitVariationsScore = values.getOrNull(headerMap["Fruitvariationsscore"] ?: -1)?.toFloatOrNull() ?: 0f,
                            grainsAndCerealsHeifaScore = values.getOrNull(headerMap["GrainsandcerealsHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            wholegrainsHeifaScore = values.getOrNull(headerMap["WholegrainsHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            meatAndAlternativesHeifaScore = values.getOrNull(headerMap["MeatandalternativesHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            dairyAndAlternativesHeifaScore = values.getOrNull(headerMap["DairyandalternativesHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            sodiumHeifaScore = values.getOrNull(headerMap["SodiumHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            alcoholHeifaScore = values.getOrNull(headerMap["AlcoholHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            waterHeifaScore = values.getOrNull(headerMap["WaterHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            sugarHeifaScore = values.getOrNull(headerMap["SugarHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            saturatedFatHeifaScore = values.getOrNull(headerMap["SaturatedFatHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            unsaturatedFatHeifaScore = values.getOrNull(headerMap["UnsaturatedFatHEIFAscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f
                        )
                    } else {
                        patient = Patient(
                            userId = values.getOrNull(headerMap["User_ID"] ?: -1) ?: "",
                            phoneNumber = values.getOrNull(headerMap["PhoneNumber"] ?: -1) ?: "",
                            sex = sex,
                            heifaTotalScore = values.getOrNull(headerMap["HEIFAtotalscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            discretionaryHeifaScore = values.getOrNull(headerMap["DiscretionaryHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            vegetablesHeifaScore = values.getOrNull(headerMap["VegetablesHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitHeifaScore = values.getOrNull(headerMap["FruitHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitSizeScore = values.getOrNull(headerMap["Fruitservesize"] ?: -1)?.toFloatOrNull() ?: 0f,
                            fruitVariationsScore = values.getOrNull(headerMap["Fruitvariationsscore"] ?: -1)?.toFloatOrNull() ?: 0f,
                            grainsAndCerealsHeifaScore = values.getOrNull(headerMap["GrainsandcerealsHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            wholegrainsHeifaScore = values.getOrNull(headerMap["WholegrainsHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            meatAndAlternativesHeifaScore = values.getOrNull(headerMap["MeatandalternativesHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            dairyAndAlternativesHeifaScore = values.getOrNull(headerMap["DairyandalternativesHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            sodiumHeifaScore = values.getOrNull(headerMap["SodiumHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            alcoholHeifaScore = values.getOrNull(headerMap["AlcoholHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            waterHeifaScore = values.getOrNull(headerMap["WaterHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            sugarHeifaScore = values.getOrNull(headerMap["SugarHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            saturatedFatHeifaScore = values.getOrNull(headerMap["SaturatedFatHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f,
                            unsaturatedFatHeifaScore = values.getOrNull(headerMap["UnsaturatedFatHEIFAscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f
                        )
                    }

                    patients.add(patient)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return patients
    }

}