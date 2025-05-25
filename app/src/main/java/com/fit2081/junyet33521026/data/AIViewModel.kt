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
    // Gemini API key
    private val apiKey = "AIzaSyCro30xkTe0iSXe7vgIMjvCMMQ1SFz1gm8"

    // respository setup for feeding data to AI
    private val patientRepository = PatientRepository(context)
    private val foodIntakeRepository = FoodIntakeRepository(context)

    // generative model instance for AI analysis
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    // UI state management
    private val _uiState: MutableStateFlow<UIState> =
        MutableStateFlow(UIState.Initial)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    /**
     * Analyse patient data using Gemini AI
     */
    fun analyseData() {
        _uiState.value = UIState.ClinicianLoading

        viewModelScope.launch {
            try {
                // feed all patients data
                val patientsList = withContext(Dispatchers.IO) {
                    patientRepository.getAllPatientsList()
                }

                // prompt to AI
                val topScoresPrompt = createTopScoresPrompt(patientsList)
                val waterIntakePrompt = createWaterIntakePrompt(patientsList)
                val genderDietPrompt = createGenderDietPrompt(patientsList)

                // AI responses
                val topScoresInsight = generateAIInsight("Top 3 Food Categories Scored by NutriTrack Users", topScoresPrompt)
                val waterInsight = generateAIInsight("Water Intake of NutriTrack Users", waterIntakePrompt)
                val genderInsight = generateAIInsight("Food Diet Pattern of Male and Female", genderDietPrompt)

                // compile insights
                val insights = listOf(topScoresInsight, waterInsight, genderInsight)
                _uiState.value = UIState.ClinicianSuccess(insights)

            } catch (e: Exception) {
                _uiState.value = UIState.ClinicianError("Error analyzing data: ${e.message}")
            }
        }
    }

    /**
     * Create a prompt to analyse water intake patterns
     */
    private fun createWaterIntakePrompt(patients: List<Patient>): String {
        val waterData = patients.joinToString("\n") { patient ->
            "Patient ${patient.userId}: Water Score: ${patient.waterHeifaScore}, Total HEIFA: ${patient.heifaTotalScore}"
        }

        return """
            As a professional nutrition clinician, 
            analyse water intake patterns from these HEIFA water scores:
            $waterData
            
            Describe the overall water intake patterns of users in the database in 2-3 sentences.
            Mention score ranges and any correlation with overall health outcomes.
            Focus on general patterns, not individual users.
        """.trimIndent()
    }

    /**
     * Create a prompt to analyse top 3 highest food scores
     */
    private fun createTopScoresPrompt(patients: List<Patient>): String {
        val foodScoreData = patients.joinToString("\n") { patient ->
            """
                Patient Profile:
                Name: ${patient.name}
                Sex: ${patient.sex}
                Total HEIFA Score: ${patient.heifaTotalScore}
                
                Patient Food Scores:
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
           """
        }

        return """
            As a professional nutrition clinician, analyse these food category HEIFA scores and identify the top 3 food categories that users score highest in:
            $foodScoreData
            
            Identify which 3 food categories consistently have the highest scores across 
            users with reasonable justification in 2-3 sentences.
            Focus on patterns and trends, not individual patient data.
        """.trimIndent()
    }

    /**
     * Create a prompt to analyse male and female food diet variations
     */
    private fun createGenderDietPrompt(patients: List<Patient>): String {
        val malePatients = patients.filter { it.sex.equals("Male", ignoreCase = true) }
        val femalePatients = patients.filter { it.sex.equals("Female", ignoreCase = true) }

        val maleData = malePatients.joinToString("\n") { patient ->
            """Male Patient Dataset: 

                Name: ${patient.name}
                Sex: ${patient.sex}
                Total HEIFA Score: ${patient.heifaTotalScore}
                
                Patient Food Scores:
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
           
            """.trimMargin()
        }

        val femaleData = femalePatients.joinToString("\n") { patient ->
            """Female Patient Dataset: 

                Name: ${patient.name}
                Sex: ${patient.sex}
                Total HEIFA Score: ${patient.heifaTotalScore}
                
                Patient Food Scores:
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
           
            """.trimMargin()
        }

        return """
            As a professional nutrition clinician, 
            compare male and female food diet variations based on all the food scores:
            
            All Male Patients Dataset:
            $maleData
            
            All Female Patients Dataset:
            $femaleData
            
            Analyse how male and female food diet patterns vary in 2-3 sentences.
            Focus on differences in food category preferences and overall dietary patterns between genders.
        """.trimIndent()
    }

    /**
     * Generate an AI insight based on a prompt
     */
    private suspend fun generateAIInsight(title: String, prompt: String): ClinicianInsight {
        return try {
            // AI response
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
     * Generate motivational message for NutriCoach
     */
    fun generateMotivationalMessage(patientId: String) {
        _uiState.value = UIState.NutriCoachLoading

        viewModelScope.launch {
            try {
                // feed all data of that patient
                val patient = withContext(Dispatchers.IO) {
                    patientRepository.getPatient(patientId)
                }
                val latestFoodIntake = withContext(Dispatchers.IO) {
                    foodIntakeRepository.getLatestFoodIntake(patientId)
                }

                // prompt to AI
                val motivationalPrompt = createMotivationalPrompt(patient, latestFoodIntake)

                // AI response
                val response = generativeModel.generateContent(
                    content {
                        text(motivationalPrompt)
                    }
                )

                // compile message
                val message = response.text?.trim() ?: "Keep up the good work with your nutrition journey!"
                _uiState.value = UIState.NutriCoachSuccess(message)

            } catch (e: Exception) {
                _uiState.value = UIState.NutriCoachError("Error generating message: ${e.message}")
            }
        }
    }

    /**
     * Generate NutriAI response from custom nutrition questions
     */
    fun generateNutritionResponse(patientId: String, userQuestion: String) {
        _uiState.value = UIState.AIChatLoading

        viewModelScope.launch {
            try {
                // feed all data of that patient
                val patient = withContext(Dispatchers.IO) {
                    patientRepository.getPatient(patientId)
                }
                val foodIntakes = withContext(Dispatchers.IO) {
                    foodIntakeRepository.getFoodIntakesForPatient(patientId)
                }

                // prompt to AI
                val aiChatPrompt = createAIChatPrompt(patient, foodIntakes, userQuestion)

                // AI response
                val response = generativeModel.generateContent(
                    content {
                        text(aiChatPrompt)
                    }
                )

                // compile response
                val message = response.text?.trim() ?: "I'm sorry, I couldn't generate a response to your question. Please try rephrasing your question."
                _uiState.value = UIState.AIChatSuccess(message)

            } catch (e: Exception) {
                _uiState.value = UIState.AIChatError("Error generating response: ${e.message}")
            }
        }
    }

    /**
     * Create comprehensive AI chat prompt with all patient data
     */
    private fun createAIChatPrompt(patient: Patient, foodIntakes: List<FoodIntake>, userQuestion: String): String {
        val foodIntakeHistory = if (foodIntakes.isNotEmpty()) {
            foodIntakes.takeLast(10).joinToString("\n") { intake ->
                "Foods: ${intake.selectedFoods}, Persona: ${intake.persona}, Meal Time: ${intake.mealTime}, Sleep Time: ${intake.sleepTime}, Wake Time: ${intake.wakeTime}"
            }
        } else {
            "No food intake data available"
        }

        return """
        As a professional nutrition AI assistant (named NutriAI) with access to comprehensive patient data. 
        Please provide a detailed, informative response (minimum 5 sentences) to the user's question.
        
        Patient Profile:
        Name: ${patient.name}
        Sex: ${patient.sex}
        Total HEIFA Score: ${patient.heifaTotalScore}
        
        Patient Food Scores:
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
        
        Patient Food Intake History:
        $foodIntakeHistory
        
        Patient's Question: "$userQuestion"
        
        Please provide a comprehensive response that:
        1. Directly answers the user's question based on their specific data
        2. Provides personalised insights and explanations
        3. Offers practical tips and actionable recommendations
        4. Includes relevant nutritional science where appropriate
        5. Suggests helpful resources or websites if applicable (use real, reputable sources like World Health Organisation etc.)
        
        Keep the tone friendly, professional and encouraging. Make sure your response is at most
         5 sentences long and provides valuable, actionable information. If you have large 
         information chunk to share, please break it down into smaller sections with bold header 
         title, with this, you can have 5 sentences long for each response section.
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
            
        Patient Profile:
        Name: ${patient.name}
        Sex: ${patient.sex}
        Total HEIFA Score: ${patient.heifaTotalScore}
        
        Patient Food Scores:
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
            
        Patient Latest Food Intake:
        $foodIntakeInfo
            
        Based on the above data, generate a personalised, encouraging, and actionable motivational message or fun food tip in 2-3 sentences. 
        Focus on their strengths and provide gentle suggestions for improvement. Make it positive and engaging.
        For every new motivational message generated, please talk on something else and not 
        repeating the same strength and improvements, tackle on different food categories.
        """.trimIndent()
    }





    /**
     * AIViewModel constructor.
     */
    class AIViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AIViewModel(context.applicationContext) as T
        }
    }
}