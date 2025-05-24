package com.fit2081.junyet33521026.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

/**
 * NutriCoachTipViewModel separates the UI from the data layer to manage NutriCoachTip entities.
 * @param context Context to access application resources and database.
 * @return NutriCoachTipViewModel instance.
 */
class NutriCoachTipViewModel(context: Context) : ViewModel() {
    // Repository for managing NutriCoachTip data
    private val tipRepository = NutriCoachTipRepository(context)

    /**
     * Saves a NutriCoachTip for a specific patient.
     * @param patientId The ID of the patient.
     * @param tipMessage The message of the tip to be saved.
     */
    suspend fun saveTip(patientId: String, tipMessage: String) {
        val tip = NutriCoachTip(
            patientId = patientId,
            tipMessage = tipMessage
        )
        tipRepository.insertTip(tip)
    }

    /**
     * Retrieves the latest NutriCoachTip for a specific patient.
     * @param patientId The ID of the patient.
     * @return The latest NutriCoachTip for the patient, or null if none exists.
     */
    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip> =
        tipRepository.getTipsForPatient(patientId)

    /**
     * Deletes all NutriCoachTips for a specific patient.
     * @param patientId The ID of the patient whose tips are to be deleted.
     */
    suspend fun deleteAllTipsForPatient(patientId: String) =
        tipRepository.deleteAllTipsForPatient(patientId)

    /**
     * NutriCoachTipViewModel constructor.
     * @param context Context to access application resources and database.
     */
    class NutriCoachTipViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NutriCoachTipViewModel(context.applicationContext) as T
        }
    }
}
