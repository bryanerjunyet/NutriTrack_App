package com.fit2081.junyet33521026.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * PatientRepository class acts as a mediator between the database and application's UI
 * that provides methods to interact with the PatientDao and perform operations related to patients.
 * @param context Context to access application resources and database.
 */
class PatientRepository(context: Context) {
    // PatientDao instance to perform database operations
    private val patientDao: PatientDao

    // Constructor to setup PatientDao
    init {
        patientDao = NutriTrackDatabase.getDatabase(context).patientDao()
    }

    /////////// Basic Operations /////////

    /**
     * Retrieves a patient by their user ID.
     * @param userId Patient ID.
     * @return The patient corresponding to the user ID.
     */
    suspend fun getPatient(userId: String): Patient = patientDao.getPatient(userId)

    /**
     * Retrieves all patients in the database.
     * @return A flow of a list of all patients.
     */
    fun getAllPatients(): Flow<List<Patient>> = patientDao.getAllPatients()

    /**
     * Retrieves all patients in the database.
     * @return A list of all patients.
     */
    suspend fun getAllPatientsList(): List<Patient> = patientDao.getAllPatientsList()

    /**
     * Retrieves all user IDs in the database.
     * @return A flow of a list of user IDs.
     */
    fun getAllUserIds(): Flow<List<String>> = patientDao.getAllUserIds()

    /**
     * Inserts a new patient into the database.
     * @param patient The patient to be inserted.
     */
    suspend fun updatePatient(patient: Patient) = patientDao.update(patient)


    ///////// Advance Operation for Score Statistics /////////

    /**
     * Calculate average food score of all male patients in the database.
     * @return Average male patient food score.
     */
    suspend fun getAverageHeifaScoreMale(): Float = patientDao.getAverageHeifaScoreMale()

    /**
     * Calculate average food score of all female patients in the database.
     * @return Average female patient food score.
     */
    suspend fun getAverageHeifaScoreFemale(): Float = patientDao.getAverageHeifaScoreFemale()

    /**
     * Retrieves all scores from the database.
     * @return A list of all scores.
     */
    suspend fun getAllScores(): List<Float> = patientDao.getAllScores()

    /**
     * Retrieves the minimum score from the database.
     * @return The minimum score.
     */
    suspend fun getMinScore(): Float = patientDao.getMinScore()

    /**
     * Retrieves the maximum score from the database.
     * @return The maximum score.
     */
    suspend fun getMaxScore(): Float = patientDao.getMaxScore()

    /**
     * Retrieves the score distribution from the database.
     * @return A list of score frequencies.
     */
    suspend fun getScoreDistribution(): List<ScoreFrequency> = patientDao.getScoreDistribution()

    /**
     * Calculates the percentile of a given score based on the score distribution.
     * @param userScore The score to calculate the percentile for.
     * @return The percentile of the given score.
     */
    suspend fun calculatePercentile(userScore: Float): Float {
        val scorePosition = patientDao.getScorePosition(userScore)
        val totalPatient = patientDao.getTotalPatient()
        return if (totalPatient > 1) {
            (scorePosition.toFloat() / (totalPatient - 1)) * 100
        } else 0f
    }

    /**
     * Calculates the median score from the list of all scores.
     * @return The median score.
     */
    suspend fun calculateMedianScore(): Float {
        val allScores = getAllScores()
        return if (allScores.isEmpty()) {
            0f
        } else {
            val middleIndex = allScores.size / 2
            if (allScores.size % 2 == 0) {
                // Even number of scores - average of two middle values
                (allScores[middleIndex - 1] + allScores[middleIndex]) / 2
            } else {
                // Odd number of scores - middle value
                allScores[middleIndex]
            }
        }
    }


    /////// Load Database Operation /////////

    /**
     * Validates the registration of a patient based on user ID and phone number.
     * @param context The application context.
     */
    suspend fun importPatientsFromCsv(context: Context) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean("csv_imported", false)) {
            val patients = parseCsv(context)
            patients.forEach { patientDao.insert(it) }
            sharedPref.edit().putBoolean("csv_imported", true).apply()
        }
    }

    /**
     * Parses a CSV file and returns a list of Patient data.
     * @param context The application context.
     * @return A list of Patient data.
     */
    private fun parseCsv(context: Context): List<Patient> {
        val patients = mutableListOf<Patient>()
        val assets = context.assets

        try {
            val inputStream = assets.open("nutritrack_users.csv")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // read header row to map column names
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
                            fruitSize = values.getOrNull(headerMap["Fruitservesize"] ?: -1)?.toFloatOrNull() ?: 0f,
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
                            fruitSize = values.getOrNull(headerMap["Fruitservesize"] ?: -1)?.toFloatOrNull() ?: 0f,
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