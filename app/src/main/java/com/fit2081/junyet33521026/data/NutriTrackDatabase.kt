package com.fit2081.junyet33521026.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Patient::class, FoodIntake::class],
    version = 1,
    exportSchema = false
)
abstract class NutriTrackDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao

    companion object {
        @Volatile
        private var INSTANCE: NutriTrackDatabase? = null

        fun getDatabase(context: Context): NutriTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriTrackDatabase::class.java,
                    "nutritrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}