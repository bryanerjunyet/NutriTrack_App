package com.fit2081.junyet33521026.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

// ViewModel for NutriCoach Tips
class NutriCoachTipViewModel(context: Context) : ViewModel() {
    private val tipRepository = NutriCoachTipRepository(context)

    suspend fun saveTip(patientId: String, tipMessage: String) {
        val tip = NutriCoachTip(
            patientId = patientId,
            tipMessage = tipMessage
        )
        tipRepository.insertTip(tip)
    }

    suspend fun getTipsForPatient(patientId: String): List<NutriCoachTip> =
        tipRepository.getTipsForPatient(patientId)

    suspend fun deleteAllTipsForPatient(patientId: String) =
        tipRepository.deleteAllTipsForPatient(patientId)

    class NutriCoachTipViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NutriCoachTipViewModel(context.applicationContext) as T
        }
    }
}
