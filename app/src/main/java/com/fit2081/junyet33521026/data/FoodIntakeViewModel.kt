package com.fit2081.junyet33521026.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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

    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? =
        foodIntakeRepo.getLatestFoodIntake(patientId)

    class FoodIntakeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FoodIntakeViewModel(context.applicationContext) as T
        }
    }
}