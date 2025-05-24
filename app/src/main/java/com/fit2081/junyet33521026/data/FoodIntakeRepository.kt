package com.fit2081.junyet33521026.data

import android.content.Context

/**
 * FoodIntakeRepository acts as a data repository for managing FoodIntake entities
 * in the database that provides methods to insert, retrieve and update food intake records for patients.
 * @param context Context to access application resources and database.
 */
class FoodIntakeRepository(context: Context) {
    // FoodIntakeDao instance for accessing FoodIntake data
    private val foodIntakeDao: FoodIntakeDao

    // Constructor to setup FoodIntakeDao instance
    init {
        foodIntakeDao = NutriTrackDatabase.getDatabase(context).foodIntakeDao()
    }

    /**
     * Inserts a new FoodIntake into the database.
     * @param foodIntake The FoodIntake to be inserted.
     */
    suspend fun insertFoodIntake(foodIntake: FoodIntake) = foodIntakeDao.insert(foodIntake)

    /**
     * Updates an existing FoodIntake in the database.
     * @param foodIntake The FoodIntake to be updated.
     */
    suspend fun updateFoodIntake(foodIntake: FoodIntake) = foodIntakeDao.update(foodIntake)

    /**
     * Retrieves the latest FoodIntake for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest FoodIntake for the patient, or null if none exists.
     */
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? =
        foodIntakeDao.getLatestFoodIntake(patientId)

    /**
     * Retrieves all FoodIntakes for a specific patient.
     * @param patientId The ID of the patient.
     * @return A list of FoodIntake records for the patient.
     */
    suspend fun getFoodIntakesForPatient(patientId: String): List<FoodIntake> =
        foodIntakeDao.getFoodIntakesForPatient(patientId)
}