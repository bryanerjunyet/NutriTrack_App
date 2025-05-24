package com.fit2081.junyet33521026.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
/**
 * Data Access Object for managing food intake data in the database
 * which provides methods to insert, update and query food intake records.
 */
interface FoodIntakeDao {

    @Insert
    /**
     * Inserts a new food intake record into the database.
     * @param foodIntake The FoodIntake instance to be inserted.
     */
    suspend fun insert(foodIntake: FoodIntake)

    @Update
    /**
     * Updates an existing food intake record in the database.
     * @param foodIntake The FoodIntake instance with updated data.
     */
    suspend fun update(foodIntake: FoodIntake)

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId ORDER BY timestamp DESC LIMIT 1")
    /**
     * Retrieves the latest food intake record for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest FoodIntake record for the patient, or null if none exists.
     */
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake?

    @Query("SELECT * FROM food_intakes WHERE patientId = :patientId")
    /**
     * Retrieves all food intake records for a specific patient.
     * @param patientId The ID of the patient.
     * @return A list of FoodIntake records for the patient.
     */
    suspend fun getFoodIntakesForPatient(patientId: String): List<FoodIntake>
}