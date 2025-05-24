package com.fit2081.junyet33521026.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Patient::class, FoodIntake::class, NutriCoachTip::class],
    version = 1,
    exportSchema = false
)
/**
 * NutriTrackDatabase is the Room database class for the NutriTrack application
 * that defines the database schema and provides access to DAOs for managing
 * Patient, FoodIntake, and NutriCoachTip entities.
 */
abstract class NutriTrackDatabase : RoomDatabase() {
    // Data Access Objects (DAOs) for accessing the database
    abstract fun patientDao(): PatientDao
    abstract fun foodIntakeDao(): FoodIntakeDao
    abstract fun nutriCoachTipDao(): NutriCoachTipDao

    companion object {
        @Volatile
        /**
         * Volatile variable that holds the singleton instance of NutriTrackDatabase.
         */
        private var INSTANCE: NutriTrackDatabase? = null

        /**
         * Provides a singleton instance of NutriTrackDatabase.
         * @param context Context to access application resources and database.
         * @return Instance of NutriTrackDatabase.
         */
        fun getDatabase(context: Context): NutriTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriTrackDatabase::class.java,
                    "nutritrack_database"
                )
                    .fallbackToDestructiveMigration(false) // handle version changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}