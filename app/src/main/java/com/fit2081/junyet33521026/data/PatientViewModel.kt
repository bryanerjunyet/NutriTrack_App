package com.fit2081.junyet33521026.data

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
        Log.d("PatientViewModel", "validateRegistration: $userId, $phoneNumber")
        val patient = patientRepo.getPatient(userId)
        Log.d("PatientViewModel", "validateRegistration: $patient")
        Log.d("PatientViewModel", "validateRegistration: ${patient.phoneNumber == phoneNumber}")
        return patient.phoneNumber == phoneNumber
    }

    suspend fun validateCredentials(userId: String, password: String): Boolean {
        Log.d("PatientViewModel", "validateCredentials: $userId, $password")
        val patient = patientRepo.getPatient(userId)
        Log.d("PatientViewModel", "validateCredentials: $patient")
        Log.d("PatientViewModel", "validateCredentials (password): ${patient.password}")
        Log.d("PatientViewModel", "validateCredentials: ${patient.password == password}")
        return patient.password == password
    }

    suspend fun registerPatient(
        userId: String,
        phoneNumber: String,
        name: String,
        password: String
    ): Boolean {
        Log.d("PatientViewModel", "registerPatient: $userId, $phoneNumber, $name, $password")
        val patient =(patientRepo.getPatient(userId))
        Log.d("PatientViewModel", "registerPatient: $patient")
        if (patient == null) {
            return false
        }
        if (patient.phoneNumber != phoneNumber) return false

        patient.name = name
        patient.password = password
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