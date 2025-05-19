package com.fit2081.junyet33521026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "food_intakes",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FoodIntake(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val selectedFoods: String, // JSON string of selected foods
    val persona: String,
    val mealTime: String,
    val sleepTime: String,
    val wakeTime: String,
    val timestamp: Long = System.currentTimeMillis()
)