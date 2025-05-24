package com.fit2081.junyet33521026.data

import androidx.room.*


@Entity(
    tableName = "nutri_coach_tips",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["userId"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
/**
 * Data class for a NutriCoachTip entity in the database.
 */
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val tipMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)