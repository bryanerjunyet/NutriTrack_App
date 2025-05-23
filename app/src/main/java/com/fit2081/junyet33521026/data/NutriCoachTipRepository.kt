package com.fit2081.junyet33521026.data

import androidx.room.*
import android.content.Context

// Repository for NutriCoach Tips
class NutriCoachTipRepository(context: Context) {
    private val tipDao: NutriCoachTipDao

    init {
        tipDao = NutriTrackDatabase.getDatabase(context).nutriCoachTipDao()
    }

    suspend fun insertTip(tip: NutriCoachTip) = tipDao.insert(tip)

    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip> =
        tipDao.getTipsForPatient(patientId)

    suspend fun deleteAllTipsForPatient(patientId: String) =
        tipDao.deleteAllTipsForPatient(patientId)
}