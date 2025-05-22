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
     * Analysis has completed successfully
     */
    data class Success(val insights: List<ClinicianInsight>) : UIState

    /**
     * There was an error during analysis
     */
    data class Error(val errorMessage: String) : UIState
}