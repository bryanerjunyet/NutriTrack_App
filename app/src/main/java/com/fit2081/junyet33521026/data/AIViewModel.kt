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
        _uiState.value = UIState.ClinicianLoading

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
        _uiState.value = UIState.NutriCoachLoading

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
     * Generate AI response for custom nutrition questions
     */
    fun generateCustomNutritionResponse(patientId: String, userQuestion: String) {
        _uiState.value = UIState.AIChatLoading

        viewModelScope.launch {
            try {
                // Get comprehensive patient data
                val patient = withContext(Dispatchers.IO) {
                    patientDao.getPatient(patientId)
                }

                // Get all food intake data for the patient
                val foodIntakes = withContext(Dispatchers.IO) {
                    foodIntakeDao.getFoodIntakesForPatient(patientId)
                }

                // Create comprehensive prompt for AI bot
                val aiChatPrompt = processText(createAIChatPrompt(patient, foodIntakes, userQuestion))

                // Generate AI response
                val response = generativeModel.generateContent(
                    content {
                        text(aiChatPrompt)
                    }
                )

                val message = response.text?.trim() ?: "I'm sorry, I couldn't generate a response to your question. Please try rephrasing your question."
                _uiState.value = UIState.AIChatSuccess(message)

            } catch (e: Exception) {
                Log.e("AIViewModel", "Error generating AI chat response", e)
                _uiState.value = UIState.Error("Error generating response: ${e.message}")
            }
        }
    }

    /**
     * Create comprehensive AI chat prompt with all patient data
     */
    private fun createAIChatPrompt(patient: Patient, foodIntakes: List<FoodIntake>, userQuestion: String): String {
        // Compile food intake history
        val foodIntakeHistory = if (foodIntakes.isNotEmpty()) {
            foodIntakes.takeLast(10).joinToString("\n") { intake ->
                "Date: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(intake.timestamp))}, " +
                        "Foods: ${intake.selectedFoods}, Persona: ${intake.persona}, Meal Time: ${intake.mealTime}, " +
                        "Sleep Time: ${intake.sleepTime}, Wake Time: ${intake.wakeTime}"
            }
        } else {
            "No food intake data available"
        }

        return """
        You are a professional nutrition AI assistant with access to comprehensive patient data. 
        Please provide a detailed, informative response (minimum 5 sentences) to the user's question.
        
        PATIENT PROFILE:
        Name: ${patient.name}
        Sex: ${patient.sex}
        Total HEIFA Score: ${patient.heifaTotalScore}
        
        DETAILED NUTRITION SCORES:
        - Vegetables: ${patient.vegetablesHeifaScore}/10
        - Fruits: ${patient.fruitHeifaScore}/10 
        - Grains and Cereals: ${patient.grainsAndCerealsHeifaScore}/5
        - Whole Grains: ${patient.wholegrainsHeifaScore}/5
        - Meat and Alternatives: ${patient.meatAndAlternativesHeifaScore}/10
        - Dairy and Alternatives: ${patient.dairyAndAlternativesHeifaScore}/10
        - Water: ${patient.waterHeifaScore}/5
        - Sodium: ${patient.sodiumHeifaScore}/10
        - Alcohol: ${patient.alcoholHeifaScore}/5
        - Sugar: ${patient.sugarHeifaScore}/10
        - Saturated Fat: ${patient.saturatedFatHeifaScore}/5
        - Unsaturated Fat: ${patient.unsaturatedFatHeifaScore}/5
        - Discretionary Foods: ${patient.discretionaryHeifaScore}/10
        
        RECENT FOOD INTAKE HISTORY:
        $foodIntakeHistory
        
        USER'S QUESTION: "$userQuestion"
        
        Please provide a comprehensive response that:
        1. Directly answers the user's question based on their specific data
        2. Provides personalized insights and explanations
        3. Offers practical tips and actionable recommendations
        4. Includes relevant nutritional science where appropriate
        5. Suggests helpful resources or websites if applicable (use real, reputable sources like dietitians.ca, heart.org, diabetes.org, etc.)
        
        Keep the tone friendly, professional, and encouraging. Make sure your response is at most
         5 sentences long and provides valuable, actionable information. If you have large 
         information chunk to share, please break it down into smaller sections with bold header 
         title, with this, you can have 5 sentences long for 
         each 
         response section.
    """.trimIndent()
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
            - Vegetables: ${patient.vegetablesHeifaScore}/10
            - Fruits: ${patient.fruitHeifaScore}/10 
            - Grains and Cereals: ${patient.grainsAndCerealsHeifaScore}/5
            - Whole Grains: ${patient.wholegrainsHeifaScore}/5
            - Meat and Alternatives: ${patient.meatAndAlternativesHeifaScore}/10
            - Dairy and Alternatives: ${patient.dairyAndAlternativesHeifaScore}/10
            - Water: ${patient.waterHeifaScore}/5
            - Sodium: ${patient.sodiumHeifaScore}/10
            - Alcohol: ${patient.alcoholHeifaScore}/5
            - Sugar: ${patient.sugarHeifaScore}/10
            - Saturated Fat: ${patient.saturatedFatHeifaScore}/5
            - Unsaturated Fat: ${patient.unsaturatedFatHeifaScore}/5
            - Discretionary Foods: ${patient.discretionaryHeifaScore}/10
            
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

    fun processText(input: String): String {
        // Remove empty lines and process each line
        val trimmedLines = input.lines()
            .filter { it.isNotBlank() } // Remove empty lines
            .joinToString("\n") { line ->
                // Replace sentences wrapped with ** to bold
                line.replace(Regex("\\*\\*(.*?)\\*\\*")) { matchResult ->
                    "<b>${matchResult.groupValues[1]}</b>"
                }
            }
        return trimmedLines
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