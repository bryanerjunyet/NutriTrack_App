package com.fit2081.junyet33521026.data


/**
 * A sealed hierarchy describing the state of the clinician dashboard UI.
 */
sealed interface ClinicianUiState {
    /**
     * Empty state when the screen is first shown
     */
    object Initial : ClinicianUiState

    /**
     * Still loading data or waiting for AI response
     */
    object Loading : ClinicianUiState

    /**
     * Analysis has completed successfully
     */
    data class Success(val insights: List<ClinicianInsight>) : ClinicianUiState

    /**
     * There was an error during analysis
     */
    data class Error(val errorMessage: String) : ClinicianUiState
}