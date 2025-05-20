package com.fit2081.junyet33521026.data

import android.content.Context

class FoodIntakeRepository(context: Context) {
    private val foodIntakeDao: FoodIntakeDao

    init {
        foodIntakeDao = NutriTrackDatabase.getDatabase(context).foodIntakeDao()
    }

    suspend fun insertFoodIntake(foodIntake: FoodIntake) = foodIntakeDao.insert(foodIntake)

    suspend fun updateFoodIntake(foodIntake: FoodIntake) = foodIntakeDao.update(foodIntake)

    suspend fun getLatestFoodIntake(patientId: String): FoodIntake? =
        foodIntakeDao.getLatestFoodIntake(patientId)

    suspend fun getFoodIntakesForPatient(patientId: String): List<FoodIntake> =
        foodIntakeDao.getFoodIntakesForPatient(patientId)
}