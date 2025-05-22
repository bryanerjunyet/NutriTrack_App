package com.fit2081.junyet33521026.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.junyet33521026.data.NutriTrackDatabase
import com.fit2081.junyet33521026.data.Patient
import com.fit2081.junyet33521026.data.PatientDao
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Clinician Dashboard
 */
class ClinicianViewModel(context: Context) : ViewModel() {
    private val patientDao: PatientDao = NutriTrackDatabase.getDatabase(context).patientDao()

    // API key for Gemini AI
    private val apiKey = "AIzaSyCro30xkTe0iSXe7vgIMjvCMMQ1SFz1gm8"

    // GenerativeModel instance for AI analysis
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    // UI state for the dashboard
    private val _uiState: MutableStateFlow<ClinicianUiState> =
        MutableStateFlow(ClinicianUiState.Initial)
    val uiState: StateFlow<ClinicianUiState> = _uiState.asStateFlow()

    // Average HEIFA scores by gender
    private val _maleAverageHeifa = MutableStateFlow(0.0f)
    val maleAverageHeifa: StateFlow<Float> = _maleAverageHeifa

    private val _femaleAverageHeifa = MutableStateFlow(0.0f)
    val femaleAverageHeifa: StateFlow<Float> = _femaleAverageHeifa

    /**
     * Calculate average HEIFA scores for males and females
     */
    fun calculateAverageScores() {
        viewModelScope.launch {
            try {
                patientDao.getAllPatients().collect { patients ->
                    val malePatients = patients.filter { it.sex.equals("Male", ignoreCase = true) }
                    val femalePatients = patients.filter { it.sex.equals("Female", ignoreCase = true) }

                    val maleAvg = if (malePatients.isNotEmpty()) {
                        malePatients.map { it.heifaTotalScore }.average().toFloat()
                    } else 0.0f

                    val femaleAvg = if (femalePatients.isNotEmpty()) {
                        femalePatients.map { it.heifaTotalScore }.average().toFloat()
                    } else 0.0f

                    _maleAverageHeifa.value = maleAvg
                    _femaleAverageHeifa.value = femaleAvg
                }
            } catch (e: Exception) {
                // Handle error
                _uiState.value = ClinicianUiState.Error("Error calculating averages: ${e.message}")
            }
        }
    }

    /**
     * Analyze patient data using Gemini AI
     */
    fun analyzeData() {
        _uiState.value = ClinicianUiState.Loading

        viewModelScope.launch {
            try {
                // Get all patients
                val patientsList = withContext(Dispatchers.IO) {
                    patientDao.getAllPatientsList()
                }

                // Create prompts for analysis
                val topScoresPrompt = createTopScoresPrompt(patientsList)
                val waterIntakePrompt = createWaterIntakePrompt(patientsList)
                val genderDietPrompt = createGenderDietPrompt(patientsList)

                // Get AI responses
                val topScoresInsight = generateAIInsight("Top 3 Food Categories Scored by NutriTrack Users", topScoresPrompt)
                val waterInsight = generateAIInsight("Water Intake of NutriTrack Users", waterIntakePrompt)
                val genderInsight = generateAIInsight("Food Diet Pattern of Male and Female", genderDietPrompt)

                // Compile insights
                val insights = listOf(topScoresInsight, waterInsight, genderInsight)
                _uiState.value = ClinicianUiState.Success(insights)

            } catch (e: Exception) {
                _uiState.value = ClinicianUiState.Error("Error analyzing data: ${e.message}")
            }
        }
    }

    /**
     * Generate an AI insight based on a prompt
     */
    private suspend fun generateAIInsight(title: String, prompt: String): ClinicianInsight {
        return try {
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                }
            )

            val description = response.text?.trim() ?: "No analysis available"
            ClinicianInsight(title, description)
        } catch (e: Exception) {
            ClinicianInsight(title, "Error generating insight: ${e.message}")
        }
    }

    /**
     * Create a prompt to analyze water intake patterns
     */
    private fun createWaterIntakePrompt(patients: List<Patient>): String {
        val waterData = patients.joinToString("\n") { patient ->
            "Patient ${patient.userId}: Water Score: ${patient.waterHeifaScore}, Total HEIFA: ${patient.heifaTotalScore}"
        }

        return """
            Analyze water intake patterns from these HEIFA water scores:
            $waterData
            
            Describe the overall water intake patterns of users in the database in 2-3 sentences.
            Mention score ranges and any correlation with overall health outcomes.
            Focus on general patterns, not individual users.
        """.trimIndent()
    }

    /**
     * Create a prompt to analyze top 3 highest food scores
     */
    private fun createTopScoresPrompt(patients: List<Patient>): String {
        val foodScoreData = patients.joinToString("\n") { patient ->
            """Patient ${patient.userId}: 
               Vegetables: ${patient.vegetablesHeifaScore}
               Fruits: ${patient.fruitHeifaScore}
               Grains: ${patient.grainsAndCerealsHeifaScore}
               Wholegrains: ${patient.wholegrainsHeifaScore}
               Meat/Alt: ${patient.meatAndAlternativesHeifaScore}
               Dairy/Alt: ${patient.dairyAndAlternativesHeifaScore}
               Water: ${patient.waterHeifaScore}
               Discretionary: ${patient.discretionaryHeifaScore}"""
        }

        return """
            Analyze these food category HEIFA scores and identify the top 3 food categories that users score highest in:
            $foodScoreData
            
            Identify which 3 food categories consistently have the highest scores across users in 2-3 sentences.
            Focus on patterns and trends, not individual patient data.
        """.trimIndent()
    }

    /**
     * Create a prompt to analyze male and female food diet variations
     */
    private fun createGenderDietPrompt(patients: List<Patient>): String {
        val malePatients = patients.filter { it.sex.equals("Male", ignoreCase = true) }
        val femalePatients = patients.filter { it.sex.equals("Female", ignoreCase = true) }

        val maleData = malePatients.joinToString("\n") { patient ->
            """Male Patient: Vegetables: ${patient.vegetablesHeifaScore}, Fruits: ${patient.fruitHeifaScore}, 
               Grains: ${patient.grainsAndCerealsHeifaScore}, Meat: ${patient.meatAndAlternativesHeifaScore}, 
               Dairy: ${patient.dairyAndAlternativesHeifaScore}, Total: ${patient.heifaTotalScore}"""
        }

        val femaleData = femalePatients.joinToString("\n") { patient ->
            """Female Patient: Vegetables: ${patient.vegetablesHeifaScore}, Fruits: ${patient.fruitHeifaScore}, 
               Grains: ${patient.grainsAndCerealsHeifaScore}, Meat: ${patient.meatAndAlternativesHeifaScore}, 
               Dairy: ${patient.dairyAndAlternativesHeifaScore}, Total: ${patient.heifaTotalScore}"""
        }

        return """
            Compare male and female food diet variations based on these HEIFA scores:
            
            MALE PATIENTS:
            $maleData
            
            FEMALE PATIENTS:
            $femaleData
            
            Analyze how male and female food diet patterns vary in 2-3 sentences.
            Focus on differences in food category preferences and overall dietary patterns between genders.
        """.trimIndent()
    }

    /**
     * Factory for creating ClinicianViewModel instances
     */
    class ClinicianViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClinicianViewModel(context.applicationContext) as T
        }
    }
}