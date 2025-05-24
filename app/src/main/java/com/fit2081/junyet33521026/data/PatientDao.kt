package com.fit2081.junyet33521026.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
/**
 * Data Access Object for managing patient data in the database
 * which provides methods to insert, update and query patient data.
 */
interface PatientDao {

    ////////// Basic Operations /////////

    @Insert
    /**
     * Inserts a new patient into the database.
     * @param patient The patient to be inserted.
     */
    suspend fun insert(patient: Patient)

    @Update
    /**
     * Updates an existing patient in the database.
     * @param patient The patient with updated data.
     */
    suspend fun update(patient: Patient)

    @Query("SELECT * FROM patients WHERE userId = :userId")
    /**
     * Retrieves a patient by their user ID.
     * @param userId Patient ID.
     * @return The patient corresponding to the user ID.
     */
    suspend fun getPatient(userId: String): Patient

    @Query("SELECT * FROM patients ORDER BY userId ASC")
    /**
     * Retrieves all patients in the database.
     * @return A flow of a list of all patients.
     */
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients ORDER BY userId ASC")
    /**
     * Retrieves all patients in the database.
     * @return A list of all patients.
     */
    suspend fun getAllPatientsList(): List<Patient>

    @Query("SELECT userId FROM patients")
    /**
     * Retrieves all user IDs in the database.
     * @return A flow of a list of user IDs.
     */
    fun getAllUserIds(): Flow<List<String>>


    /////////// Score Queries /////////

    @Query("SELECT AVG(heifaTotalScore) FROM patients WHERE sex = 'Male'")
    /**
     * Calculate average food score of all male patients in the database.
     * @return Average male patient food score.
     */
    suspend fun getAverageHeifaScoreMale(): Float

    @Query("SELECT AVG(heifaTotalScore) FROM patients WHERE sex = 'Female'")
    /**
     * Calculate average food score of all female patients in the database.
     * @return Average female patient food score.
     */
    suspend fun getAverageHeifaScoreFemale(): Float


    /////////// Score Statistics /////////

    @Query("SELECT heifaTotalScore FROM patients ORDER BY heifaTotalScore ASC")
    /**
     * Retrieves all food scores in the database.
     * @return A list of all food scores.
     */
    suspend fun getAllScores(): List<Float>

    @Query("SELECT MIN(heifaTotalScore) FROM patients")
    /**
     * Retrieves the minimum food score in the database.
     * @return The minimum food score.
     */
    suspend fun getMinScore(): Float

    @Query("SELECT MAX(heifaTotalScore) FROM patients")
    /**
     * Retrieves the maximum food score in the database.
     * @return The maximum food score.
     */
    suspend fun getMaxScore(): Float

    @Query("SELECT COUNT(*) FROM patients WHERE heifaTotalScore < :score")
    /**
     * Counts the position of a given score in the database.
     * @param score The score to find the position of.
     * @return The position of the score.
     */
    suspend fun getScorePosition(score: Float): Int

    @Query("SELECT COUNT(*) FROM patients")
    /**
     * Counts the total number of patients in the database.
     * @return The total number of patients.
     */
    suspend fun getTotalPatient(): Int

    @Query("SELECT heifaTotalScore, COUNT(*) as count FROM patients GROUP BY CAST(heifaTotalScore AS INTEGER) ORDER BY heifaTotalScore")
    /**
     * Retrieves the frequency of each score in the database.
     * @return A list of score frequencies.
     */
    suspend fun getScoreDistribution(): List<ScoreFrequency>
}