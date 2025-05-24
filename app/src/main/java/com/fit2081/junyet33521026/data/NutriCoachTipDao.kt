package com.fit2081.junyet33521026.data

import androidx.room.*

@Dao
/**
 * Data Access Object for managing NutriCoachTip data in the database
 * which provides methods to insert, query and delete tips for patients.
 */
interface NutriCoachTipDao {

    @Insert
    /**
     * Inserts a new NutriCoachTip into the database.
     * @param tip The NutriCoachTip to be inserted.
     */
    suspend fun insert(tip: NutriCoachTip)

    /**
     * Retrieves the latest NutriCoachTip for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest NutriCoachTip for the patient, or null if none exists.
     */
    @Query("SELECT * FROM nutri_coach_tips WHERE patientId = :patientId ORDER BY timestamp DESC")
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip>

    /**
     * Deletes all NutriCoachTips for a specific patient.
     * @param patientId The ID of the patient whose tips are to be deleted.
     */
    @Query("DELETE FROM nutri_coach_tips WHERE patientId = :patientId")
    suspend fun deleteAllTipsForPatient(patientId: String)

}