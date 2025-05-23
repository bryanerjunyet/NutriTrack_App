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
     * Still loading data or waiting for AI response
     */
    object Loading : UIState

    /**
     * Analysis has completed successfully for Clinician Insights
     */
    data class ClinicianSuccess(val insights: List<ClinicianInsight>) : UIState

    /**
     * Motivational message generated successfully for NutriCoach
     */
    data class NutriCoachSuccess(val message: String) : UIState

    /**
     * There was an error during analysis
     */
    data class Error(val errorMessage: String) : UIState
}