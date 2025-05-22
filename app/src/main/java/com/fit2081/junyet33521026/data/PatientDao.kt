package com.fit2081.junyet33521026.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert
    suspend fun insert(patient: Patient)

    @Update
    suspend fun update(patient: Patient)

    @Query("SELECT * FROM patients WHERE userId = :userId")
    suspend fun getPatient(userId: String): Patient

    @Query("SELECT * FROM patients ORDER BY userId ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients ORDER BY userId ASC")
    suspend fun getAllPatientsList(): List<Patient>

    @Query("SELECT userId FROM patients")
    fun getAllUserIds(): Flow<List<String>>

    @Query("SELECT AVG(heifaTotalScore) FROM patients WHERE sex = 'Male'")
    suspend fun getAverageHeifaScoreMale(): Float

    @Query("SELECT AVG(heifaTotalScore) FROM patients WHERE sex = 'Female'")
    suspend fun getAverageHeifaScoreFemale(): Float
}