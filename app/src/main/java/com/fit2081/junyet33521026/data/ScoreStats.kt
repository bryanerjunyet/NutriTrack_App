package com.fit2081.junyet33521026.data

/**
 * Data class of statistics of a patient's food scores.
 *
 * @property minScore The minimum score of the patient.
 * @property maxScore The maximum score of the patient.
 * @property medianScore The median score of the patient.
 * @property userPercentile The percentile rank of the patient compared to others.
 * @property distribution A list of score frequencies.
 */
data class ScoreStats(
    val minScore: Float,
    val maxScore: Float,
    val medianScore: Float,
    val userPercentile: Float,
    val distribution: List<ScoreFrequency>
)

/**
 * Data class of the frequency of a specific score in the database.
 *
 * @property heifaTotalScore The score value.
 * @property count The number of patients with that score.
 */
data class ScoreFrequency(
    val heifaTotalScore: Float,
    val count: Int
)