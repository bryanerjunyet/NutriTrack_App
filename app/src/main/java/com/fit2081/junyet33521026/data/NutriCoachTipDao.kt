package com.fit2081.junyet33521026.data

import androidx.room.*

// DAO for NutriCoach Tips
@Dao
interface NutriCoachTipDao {
    @Insert
    suspend fun insert(tip: NutriCoachTip)

    @Query("SELECT * FROM nutri_coach_tips WHERE patientId = :patientId ORDER BY timestamp DESC")
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip>

    @Query("DELETE FROM nutri_coach_tips WHERE patientId = :patientId")
    suspend fun deleteAllTipsForPatient(patientId: String)

}