package com.fit2081.junyet33521026.data


import androidx.room.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import android.content.Context

// Entity for storing motivational tips
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
data class NutriCoachTip(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: String,
    val tipMessage: String,
    val timestamp: Long = System.currentTimeMillis()
)