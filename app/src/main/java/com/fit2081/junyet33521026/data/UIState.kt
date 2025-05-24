package com.fit2081.junyet33521026.data


/**
 * A sealed hierarchy describing the state of the clinician dashboard UI.
 */
sealed interface UIState {
    /**
     * Empty state when the screen is first shown
     */
    object Initial : UIState

    /**
     * Still loading data or waiting for Clinician response
     */
    object ClinicianLoading : UIState

    /**
     * Still loading data or waiting for NutriCoach Fruit Search response
     */
    object NutriCoachLoading : UIState

    /**
     * Still loading data or waiting for NutriAI Chat response
     */
    object AIChatLoading : UIState

    /**
     * Analysis has completed successfully for Clinician Insights
     */
    data class ClinicianSuccess(val insights: List<ClinicianInsight>) : UIState

    /**
     * Motivational message generated successfully for NutriCoach
     */
    data class NutriCoachSuccess(val message: String) : UIState

    /**
     * NutriAI Chat response generated successfully
     */
    data class AIChatSuccess(val message: String) : UIState

    /**
     * There was an error during generation or analysis
     */
    data class Error(val errorMessage: String) : UIState
}