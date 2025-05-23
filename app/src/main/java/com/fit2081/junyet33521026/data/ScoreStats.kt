package com.fit2081.junyet33521026.data

data class ScoreStats(
    val minScore: Float,
    val maxScore: Float,
    val medianScore: Float,
    val userPercentile: Float,
    val distribution: List<ScoreFrequency>
)

data class ScoreFrequency(
    val heifaTotalScore: Float,
    val count: Int
)