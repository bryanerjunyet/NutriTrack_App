package com.fit2081.junyet33521026.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class FoodIntakeViewModel(context: Context) : ViewModel() {
    private val foodIntakeRepo = FoodIntakeRepository(context)

    suspend fun saveFoodIntake(
        patientId: String,
        selectedFoods: List<String>,
        persona: String,
        mealTime: String,
        sleepTime: String,
        wakeTime: String
    ) {
        Log.d("FoodIntakeViewModel", "Saving food intake for patient: $patientId, foods: " +
                "$selectedFoods, persona: $persona, mealTime: $mealTime, sleepTime: $sleepTime, wakeTime: $wakeTime")
        val foodIntake = FoodIntake(
            patientId = patientId,
            selectedFoods = Json.encodeToString(selectedFoods),
            persona = persona,
            mealTime = mealTime,
            sleepTime = sleepTime,
            wakeTime = wakeTime
        )
        Log.d("FoodIntakeViewModel", "Food intake object: $foodIntake")
        foodIntakeRepo.insertFoodIntake(foodIntake)
        Log.d("FoodIntakeViewModel", "Food intake saved successfully")
    }

    suspend fun updateFoodIntake(
        id: Int,
        patientId: String,
        selectedFoods: List<String>,
        persona: String,
        mealTime: String,
        sleepTime: String,
        wakeTime: String
    ) {
        Log.d("FoodIntakeViewModel", "Updating food intake for patient: $patientId, foods: " +
                "$selectedFoods, persona: $persona, mealTime: $mealTime, sleepTime: $sleepTime, wakeTime: $wakeTime")
        val foodIntake = FoodIntake(
            id = id,
            patientId = patientId,
            selectedFoods = Json.encodeToString(selectedFoods),
            persona = persona,
            mealTime = mealTime,
            sleepTime = sleepTime,
            wakeTime = wakeTime
        )
        Log.d("FoodIntakeViewModel", "Food intake object: $foodIntake")
        foodIntakeRepo.updateFoodIntake(foodIntake)
        Log.d("FoodIntakeViewModel", "Food intake updated successfully")
    }

    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? =
        foodIntakeRepo.getLatestFoodIntake(patientId)

    class FoodIntakeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(context.applicationContext) as T
        }
    }
}