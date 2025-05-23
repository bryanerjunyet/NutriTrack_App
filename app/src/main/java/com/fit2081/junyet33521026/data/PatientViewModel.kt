package com.fit2081.junyet33521026.data

import com.fit2081.junyet33521026.utils.SHAEncrypter
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import android.util.Log

class PatientViewModel(context: Context) : ViewModel() {
    private val patientRepo = PatientRepository(context)

    val allUserIds: Flow<List<String>> = patientRepo.getAllUserIds()

    suspend fun getPatient(userId: String): Patient = patientRepo.getPatient(userId)

    suspend fun updatePatient(patient: Patient) = patientRepo.updatePatient(patient)

    suspend fun validateRegistration(userId: String, phoneNumber: String): Boolean {
        val patient = patientRepo.getPatient(userId)
        return patient.phoneNumber == phoneNumber
    }

    suspend fun validateCredentials(userId: String, password: String): Boolean {
        val patient = patientRepo.getPatient(userId)
        return patient.password == SHAEncrypter.hashPasswordSHA(password)
    }

    suspend fun registerPatient(
        userId: String,
        phoneNumber: String,
        name: String,
        password: String
    ): Boolean {
        val patient =(patientRepo.getPatient(userId))
        if (patient.phoneNumber != phoneNumber) return false

        patient.name = name
        patient.password = SHAEncrypter.hashPasswordSHA(password)
        this.updatePatient(patient)
        return true
    }

    suspend fun importPatientsFromCsv(context: Context) {
        patientRepo.importPatientsFromCsv(context)
    }

    class PatientViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientViewModel(context.applicationContext) as T
        }
    }
}