package com.fit2081.junyet33521026.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * FoodIntakeViewModel separates the UI from the data layer to manage food intake data for patients.
 * @param context Context to access application resources and database.
 * @return FoodIntakeViewModel instance.
 */
class FoodIntakeViewModel(context: Context) : ViewModel() {
    // Repository for managing food intake data
    private val foodIntakeRepo = FoodIntakeRepository(context)

    /**
     * Retrieves all food intakes for a specific patient.
     * @param patientId The ID of the patient.
     * @return A list of FoodIntake records for the patient.
     */
    suspend fun saveFoodIntake(
        patientId: String,
        selectedFoods: List<String>,
        persona: String,
        mealTime: String,
        sleepTime: String,
        wakeTime: String
    ) {
        val foodIntake = FoodIntake(
            patientId = patientId,
            selectedFoods = Json.encodeToString(selectedFoods),
            persona = persona,
            mealTime = mealTime,
            sleepTime = sleepTime,
            wakeTime = wakeTime
        )
        foodIntakeRepo.insertFoodIntake(foodIntake)
    }

    /**
     * Retrieves all food intakes for a specific patient.
     * @param patientId The ID of the patient.
     * @return A list of FoodIntake records for the patient.
     */
    suspend fun updateFoodIntake(
        id: Int,
        patientId: String,
        selectedFoods: List<String>,
        persona: String,
        mealTime: String,
        sleepTime: String,
        wakeTime: String
    ) {
        val foodIntake = FoodIntake(
            id = id,
            patientId = patientId,
            selectedFoods = Json.encodeToString(selectedFoods),
            persona = persona,
            mealTime = mealTime,
            sleepTime = sleepTime,
            wakeTime = wakeTime
        )
        foodIntakeRepo.updateFoodIntake(foodIntake)
    }

    /**
     * Retrieves the latest food intake for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest FoodIntake for the patient.
     */
    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? =
        foodIntakeRepo.getLatestFoodIntake(patientId)

    /**
     * FoodIntakeViewModel constructor.
     * @param context Context to access application resources and database.
     * @return FoodIntakeViewModel instance.
     */
    class FoodIntakeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(context.applicationContext) as T
        }
    }
}