package com.fit2081.junyet33521026.data

import com.fit2081.junyet33521026.utils.SHAEncrypter
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow

/**
 * PatientViewModel seperates application UI from the data layer to manage patient data and operations.
 * @param context Context to access application resources and database.
 * @return PatientViewModel instance.
 */
class PatientViewModel(context: Context) : ViewModel() {
    // Repository for managing patient data
    private val patientRepo = PatientRepository(context)
    val allUserIds: Flow<List<String>> = patientRepo.getAllUserIds()


    /////////// Basic Operations /////////

    /**
     * Retrieves all patients from the repository.
     * @param userId The ID of the patient to be retrieved.
     * @return The patient corresponding to the user ID.
     */
    suspend fun getPatient(userId: String): Patient = patientRepo.getPatient(userId)

    /**
     * Updates a patient in the repository.
     */
    suspend fun updatePatient(patient: Patient) = patientRepo.updatePatient(patient)


    //////////// Score Stats Operations /////////

    /**
     * Retrieves all scores from the repository.
     * @return A list of all scores.
     */
    suspend fun getAllScores(): List<Float> = patientRepo.getAllScores()

    /**
     * Retrieves the minimum score from the repository.
     * @return The minimum score.
     */
    suspend fun getMinScore(): Float = patientRepo.getMinScore()

    /**
     * Retrieves the maximum score from the repository.
     * @return The maximum score.
     */
    suspend fun getMaxScore(): Float = patientRepo.getMaxScore()

    /**
     * Retrieves the score distribution from the repository.
     * @return A list of score frequencies.
     */
    suspend fun getScoreDistribution(): List<ScoreFrequency> = patientRepo.getScoreDistribution()

    /**
     * Calculates the percentile of a given score.
     * @param userScore The score to calculate the percentile for.
     * @return The percentile of the given score.
     */
    suspend fun calculatePercentile(userScore: Float): Float = patientRepo.calculatePercentile(userScore)

    /**
     * Calculates the median score from the repository.
     * @return The median score.
     */
    suspend fun calculateMedianScore(): Float = patientRepo.calculateMedianScore()

    /**
     * Retrieves score statistics including min, max, median, user percentile, and distribution.
     * @param userScore The score to calculate the percentile for.
     * @return A ScoreStats object containing the statistics.
     */
    suspend fun getScoreStats(userScore: Float): ScoreStats {
        return ScoreStats(
            minScore = getMinScore(),
            maxScore = getMaxScore(),
            medianScore = calculateMedianScore(),
            userPercentile = calculatePercentile(userScore),
            distribution = getScoreDistribution()
        )
    }


    //////////// Registration & Validation Operations /////////

    /**
     * Validates the registration of a patient by checking if the provided phone number matches the one in the database.
     * @param userId The ID of the patient to be validated.
     * @param phoneNumber The phone number to be validated.
     * @return True or not the registration is valid.
     */
    suspend fun validateRegistration(userId: String, phoneNumber: String): Boolean {
        val patient = patientRepo.getPatient(userId)
        return patient.phoneNumber == phoneNumber
    }

    /**
     * Validates the credentials of a patient by checking if the provided password matches the one in the database.
     * @param userId The ID of the patient to be validated.
     * @param password The password to be validated.
     * @return True or not the credentials are valid.
     */
    suspend fun validateCredentials(userId: String, password: String): Boolean {
        val patient = patientRepo.getPatient(userId)
        //    *** NEW FEATURE ***    //
        // Implement hashing of password for encryption for database security
        return patient.password == SHAEncrypter.hashPasswordSHA(password)
    }

    /**
     * Registers a new patient by updating their information in the database.
     * @param userId The ID of the patient to be registered.
     * @param phoneNumber The phone number of the patient.
     * @param name The name of the patient.
     * @param password The password of the patient.
     * @return True or not the registration is successful.
     */
    suspend fun registerPatient(userId: String, phoneNumber: String, name: String, password: String): Boolean {
        val patient =(patientRepo.getPatient(userId))
        if (patient.phoneNumber != phoneNumber) return false

        patient.name = name
        patient.password = SHAEncrypter.hashPasswordSHA(password)
        this.updatePatient(patient)
        return true
    }


    ///////////// Import Operations /////////

    /**
     * Imports patients from a CSV file into the database.
     * @param context The context to access application resources.
     */
    suspend fun importPatientsFromCsv(context: Context) {
        patientRepo.importPatientsFromCsv(context)
    }

    /**
     * PatientViewModel constructor.
     * @param context Context to access application resources and database.
     */
    class PatientViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientViewModel(context.applicationContext) as T
        }
    }
}