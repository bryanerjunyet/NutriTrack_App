package com.fit2081.junyet33521026.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FoodIntakeDao {
    @Insert
    suspend fun insert(foodIntake: FoodIntake)

    @Update
    suspend fun update(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake?

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId")
    suspend fun getFoodIntakesForPatient(patientId: String): List<FoodIntake>
}