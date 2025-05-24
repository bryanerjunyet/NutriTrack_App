package com.fit2081.junyet33521026.data

import android.content.Context

/**
 * NutriCoachTipRepository acts as a data repository for managing NutriCoachTip entities
 * in the database that provides methods to insert, retrieve and delete tips for patients.
 *
 * @param context Context to access application resources and database.
 */
class NutriCoachTipRepository(context: Context) {
    // NutriCoachTipDao instance for accessing NutriCoachTip data
    private val tipDao: NutriCoachTipDao

    // Constructor to setup NutriCoachTipDao instance
    init {
        tipDao = NutriTrackDatabase.getDatabase(context).nutriCoachTipDao()
    }

    /**
     * Inserts a new NutriCoachTip into the database.
     * @param tip The NutriCoachTip to be inserted.
     */
    suspend fun insertTip(tip: NutriCoachTip) = tipDao.insert(tip)

    /**
     * Retrieves the latest NutriCoachTip for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest NutriCoachTip for the patient, or null if none exists.
     */
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip> =
        tipDao.getTipsForPatient(patientId)

    /**
     * Deletes all NutriCoachTips for a specific patient.
     * @param patientId The ID of the patient whose tips are to be deleted.
     */
    suspend fun deleteAllTipsForPatient(patientId: String) =
        tipDao.deleteAllTipsForPatient(patientId)
}