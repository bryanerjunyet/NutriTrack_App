package com.fit2081.junyet33521026.data

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for GenAI
 */
class AIViewModel(context: Context) : ViewModel() {
    private val patientDao: PatientDao = NutriTrackDatabase.getDatabase(context).patientDao()
    private val foodIntakeDao: FoodIntakeDao = NutriTrackDatabase.getDatabase(context).foodIntakeDao()

    // API key for Gemini AI
    private val apiKey = "AIzaSyCro30xkTe0iSXe7vgIMjvCMMQ1SFz1gm8"

    // GenerativeModel instance for AI analysis
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    // UI state for the dashboard
    private val _uiState: MutableStateFlow<UIState> =
        MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

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
                _uiState.value = UIState.Error("Error calculating averages: ${e.message}")
            }
        }
    }

    /**
     * Analyze patient data using Gemini AI
     */
    fun analyzeData() {
        _uiState.value = UIState.Loading

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
                _uiState.value = UIState.ClinicianSuccess(insights)

            } catch (e: Exception) {
                _uiState.value = UIState.Error("Error analyzing data: ${e.message}")
            }
        }
    }


    /**
     * Generate motivational message for NutriCoach
     */
    fun generateMotivationalMessage(patientId: String) {
        _uiState.value = UIState.Loading

        viewModelScope.launch {
            try {
                // Get patient data
                val patient = withContext(Dispatchers.IO) {
                    patientDao.getPatient(patientId)
                }

                // Get latest food intake
                val latestFoodIntake = withContext(Dispatchers.IO) {
                    foodIntakeDao.getLatestFoodIntake(patientId)
                }

                // Create prompt for motivational message
                val motivationalPrompt = createMotivationalPrompt(patient, latestFoodIntake)

                // Generate AI response
                val response = generativeModel.generateContent(
                    content {
                        text(motivationalPrompt)
                    }
                )

                val message = response.text?.trim() ?: "Keep up the great work with your nutrition journey!"
                _uiState.value = UIState.NutriCoachSuccess(message)

            } catch (e: Exception) {
                Log.e("AIViewModel", "Error generating motivational message", e)
                _uiState.value = UIState.Error("Error generating message: ${e.message}")
            }
        }
    }


    /**
     * Create motivational prompt based on patient data
     */
    private fun createMotivationalPrompt(patient: Patient, foodIntake: FoodIntake?): String {
        val foodIntakeInfo = if (foodIntake != null) {
            "Recent food intake: ${foodIntake.selectedFoods}, Persona: ${foodIntake.persona}, Meal time: ${foodIntake.mealTime}"
        } else {
            "No recent food intake data available"
        }

        return """
            Generate a motivational message or fun food tip for a patient with the following nutrition data:
            
            Name: ${patient.name}
            Sex: ${patient.sex}
            Total HEIFA Score: ${patient.heifaTotalScore}
            
            Detailed Scores:
            - Vegetables: ${patient.vegetablesHeifaScore}
            - Fruits: ${patient.fruitHeifaScore}
            - Fruit Size Score: ${patient.fruitSizeScore}
            - Fruit Variations Score: ${patient.fruitVariationsScore}
            - Grains and Cereals: ${patient.grainsAndCerealsHeifaScore}
            - Whole Grains: ${patient.wholegrainsHeifaScore}
            - Meat and Alternatives: ${patient.meatAndAlternativesHeifaScore}
            - Dairy and Alternatives: ${patient.dairyAndAlternativesHeifaScore}
            - Sodium: ${patient.sodiumHeifaScore}
            - Alcohol: ${patient.alcoholHeifaScore}
            - Water: ${patient.waterHeifaScore}
            - Sugar: ${patient.sugarHeifaScore}
            - Saturated Fat: ${patient.saturatedFatHeifaScore}
            - Unsaturated Fat: ${patient.unsaturatedFatHeifaScore}
            
            $foodIntakeInfo
            
            Based on this data, generate a personalized, encouraging, and actionable motivational message or fun food tip in 2-3 sentences. 
            Focus on their strengths and provide gentle suggestions for improvement. Make it positive and engaging.
        """.trimIndent()
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
     * Reset UI state to initial
     */
    fun resetState() {
        _uiState.value = UIState.Initial
    }

    /**
     * Factory for creating ClinicianViewModel instances
     */
    class AIViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AIViewModel(context.applicationContext) as T
        }
    }
}