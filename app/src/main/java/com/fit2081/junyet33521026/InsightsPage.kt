package com.fit2081.junyet33521026

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.junyet33521026.utils.AuthManager
import com.fit2081.junyet33521026.data.ScoreStats
import com.fit2081.junyet33521026.data.ScoreFrequency
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme


/**
 * Main activity for the application.
 */
class InsightsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InsightsPageScreen(Modifier.padding(innerPadding), patientViewModel)
                }
            }
        }
    }
}


@Composable
/**
 * Composable function for the UI of Insights Page.
 *
 * @param modifier Modifier to be applied.
 */
fun InsightsPageScreen(
    modifier: Modifier = Modifier,
    viewModel: PatientViewModel
) {
    // current context to start activity
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Insights") }
    // load current login user ID
    val currentUserID = AuthManager.currentUserId ?: return
    // load food score from CSV file
    val foodScores = remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }
    val scoreStats = remember { mutableStateOf<ScoreStats?>(null) }

    LaunchedEffect(currentUserID) {
        val patient = viewModel.getPatient(currentUserID)
        patient?.let {
            val scores = mutableListOf<Pair<String, Float>>()

            scores.add("Total Score" to patient.heifaTotalScore)
            scores.add("Discretionary" to patient.discretionaryHeifaScore)
            scores.add("Meat" to patient.meatAndAlternativesHeifaScore)
            scores.add("Fruits" to patient.fruitHeifaScore)
            scores.add("Vegetables" to patient.vegetablesHeifaScore)
            scores.add("Dairy" to patient.dairyAndAlternativesHeifaScore)
            scores.add("Sugar" to patient.sugarHeifaScore)
            scores.add("Sodium" to patient.sodiumHeifaScore)

            scores.add("Grains & Cereal" to patient.grainsAndCerealsHeifaScore)
            scores.add("Whole Grains" to patient.wholegrainsHeifaScore)
            scores.add("Alcohol" to patient.alcoholHeifaScore)
            scores.add("Water" to patient.waterHeifaScore)
            scores.add("Saturated Fat" to patient.saturatedFatHeifaScore)
            scores.add("Unsaturated Fat" to patient.unsaturatedFatHeifaScore)

            foodScores.value = scores
            // Load score statistics
            scoreStats.value = viewModel.getScoreStats(patient.heifaTotalScore)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())
    ) {
        val totalScore = foodScores.value.firstOrNull()?.second?.toInt() ?: 0
        val currentScoreStats = scoreStats.value

        // Insights title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your Insights",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Category Progress Bars
        foodScores.value.drop(1).forEachIndexed { index, (food, score) ->
            // Skip the first item (total score)
            // Full score for first 6 items is 10, the rest is 5
            val fullScore = if (index <= 6) 10 else 5
            InsightsBar(food, score, fullScore)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(28.dp))


        // Total Food Quality Score Card with Elevation
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Food Quality Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${totalScore}/100",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        totalScore < 50 -> Color(0xFFBB0E01)
                        totalScore < 70 -> Color(0xff2962ff)
                        else -> Color(0xFF2E7D32)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Progress bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { (totalScore.toFloat() / 100) },
                        modifier = Modifier
                            .weight(0.6f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = when {
                            totalScore < 50 -> Color(0xFFBB0E01)
                            totalScore < 70 -> Color(0xff2962ff)
                            else -> Color(0xFF2E7D32)
                        },
                        trackColor = Color(0xFFE0E0E0)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Score Distribution Graph Card with Median
        currentScoreStats?.let { stats ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Food Quality Score Distribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Updated chart with median line and user bar highlighting
                    ScoreDistributionChart(
                        distribution = stats.distribution,
                        userScore = totalScore.toFloat(),
                        minScore = stats.minScore,
                        maxScore = stats.maxScore,
                        medianScore = stats.medianScore
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Score range info with median
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Min: ${stats.minScore.toInt()}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Median: ${stats.medianScore.toInt()}",
                            fontSize = 15.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Max: ${stats.maxScore.toInt()}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Show percentile if available
                    Text(
                        text = "Better than ${stats.userPercentile.toInt()}% of users",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            stats.userPercentile.toInt() < 50 -> Color(0xFFBB0E01)
                            stats.userPercentile.toInt() < 70 -> Color(0xff2962ff)
                            else -> Color(0xFF2E7D32)
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))


        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Share button
            Row(modifier = Modifier.fillMaxWidth(0.6f)) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"

                        // Text message with all the insights details
                        val insightsText = StringBuilder()
                        insightsText.append("NutriTrack Food Insights\n\n")
                        insightsText.append("Total Food Score: ${totalScore.toInt()}/100\n\n")
                        insightsText.append("Food Categories Score:\n")

                        currentScoreStats?.let { stats ->
                            insightsText.append("Percentile: Better than ${stats.userPercentile.toInt()}% of users\n")
                            insightsText.append("Score Range: ${stats.minScore.toInt()}-${stats.maxScore.toInt()}\n")
                            insightsText.append("Median Score: ${stats.medianScore.toInt()}\n")
                        }

                        foodScores.value.drop(1).forEachIndexed { index, (category, score) ->
                            val fullScore = if (index < 6) 10 else 5
                            insightsText.append("- $category: ${score.toInt()}/$fullScore\n")
                        }
                        insightsText.append("\n\nShared from NutriTrack App")

                        shareIntent.putExtra(Intent.EXTRA_TEXT, insightsText.toString())
                        context.startActivity(
                            Intent.createChooser(
                                shareIntent,
                                "Share your food insights via"
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.share_icon),
                            contentDescription = "Share",
                            modifier = Modifier.size(21.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Share with friends",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Improve my diet button
            Row(modifier = Modifier.fillMaxWidth(0.6f)) {
                Button(
                    onClick = { context.startActivity(
                        Intent(
                            context,
                            NutriCoachPage::class.java
                        )
                    ) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    modifier = Modifier.weight(0.5f)
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.boost_icon),
                            contentDescription = "Share",
                            modifier = Modifier.size(23.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Improve my diet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

    }

    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

}

@Composable
/**
 * Composable function for the Insights Bar.
 *
 * @param category Food category.
 * @param score Individual score.
 * @param fullScore Full individual score.
 */
fun InsightsBar(category: String, score: Float, fullScore: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Food name
        Text(
            text = category,
            fontSize = 14.sp,
            modifier = Modifier.weight(0.3f)
        )
        // Progress bar
        LinearProgressIndicator(
            progress = { (score / fullScore) },
            modifier = Modifier
                .weight(0.6f) // lengthen the bar
                .height(10.dp) // height of the bar
                .clip(RoundedCornerShape(5.dp)), // rounded corners
            color = Color(0xFFBB0E01),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.width(2.dp))
        // Scoring
        Text(
            text = "${score.toInt()}/$fullScore",
            fontSize = 13.sp,
            modifier = Modifier.weight(0.1f)
        )
    }
}



//@Composable
//fun ScoreDistributionChart(
//    distribution: List<ScoreFrequency>,
//    userScore: Float,
//    minScore: Float,
//    maxScore: Float
//) {
//    Canvas(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(200.dp)
//            .padding(8.dp)
//    ) {
//        val canvasWidth = size.width
//        val canvasHeight = size.height
//        val barWidth = canvasWidth / distribution.size.coerceAtLeast(1)
//        val maxCount = distribution.maxOfOrNull { it.count } ?: 1
//
//        // Draw bars
//        distribution.forEachIndexed { index, scoreFreq ->
//            val barHeight = (scoreFreq.count.toFloat() / maxCount) * (canvasHeight * 0.8f)
//            val xOffset = index * barWidth
//            val yOffset = canvasHeight - barHeight
//
//            val barColor = if (scoreFreq.heifaTotalScore.toInt() == userScore.toInt()) {
//                Color(0xFFFF2222) // Green for user's score
//            } else {
//                Color(0xFFF6AEAE) // Light blue for others
//            }
//
//            drawRect(
//                color = barColor,
//                topLeft = Offset(xOffset + barWidth * 0.1f, yOffset),
//                size = Size(barWidth * 0.8f, barHeight)
//            )
//        }
//
//        // Draw user score indicator
//        val userScorePosition = ((userScore - minScore) / (maxScore - minScore)) * canvasWidth
//        drawLine(
//            color = Color(0xFF2234FF),
//            start = Offset(userScorePosition, 0f),
//            end = Offset(userScorePosition, canvasHeight),
//            strokeWidth = 4.dp.toPx(),
//            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
//        )
//    }
//}

@Composable
fun ScoreDistributionChart(
    distribution: List<ScoreFrequency>,
    userScore: Float,
    minScore: Float,
    maxScore: Float,
    medianScore: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        // Get the canvas dimensions
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate how wide each bar should be
        val numberOfBars = distribution.size.coerceAtLeast(1)
        val barWidth = canvasWidth / numberOfBars

        // Find the highest count to scale our bars properly
        val maxCount = distribution.maxOfOrNull { it.count } ?: 1

        // Leave some space at the top for the chart to look nice
        val maxBarHeight = canvasHeight * 0.8f

        // Step 1: Draw all the bars
        distribution.forEachIndexed { index, scoreData ->
            // Calculate where this bar should be positioned horizontally
            val xPosition = index * barWidth

            // Calculate how tall this bar should be based on the count
            val barHeight = (scoreData.count.toFloat() / maxCount) * maxBarHeight

            // Calculate where the bar should start vertically (from bottom up)
            val yPosition = canvasHeight - barHeight

            // Decide the color: special color for user's score, gray for others
            val barColor = if (scoreData.heifaTotalScore.toInt() == userScore.toInt()) {
                // User's score gets a special red color
                Color(0xFFBB0E01)
            } else {
                // All other scores get a light gray color
                Color(0xFFF8B6B6)
            }

            // Draw the rectangle (bar)
            drawRect(
                color = barColor,
                topLeft = Offset(
                    x = xPosition + barWidth * 0.1f, // Small margin on left
                    y = yPosition
                ),
                size = Size(
                    width = barWidth * 0.8f, // Small margin on right
                    height = barHeight
                )
            )
        }

        // Step 2: Draw the median line (CORRECTED positioning)
        if (distribution.isNotEmpty()) {
            // Find the bar that contains the median score
            var medianBarIndex = -1
            var closestDistance = Float.MAX_VALUE

            // Look through all bars to find which one has the median score
            distribution.forEachIndexed { index, scoreData ->
                val distance = kotlin.math.abs(scoreData.heifaTotalScore - medianScore)
                if (distance < closestDistance) {
                    closestDistance = distance
                    medianBarIndex = index
                }
            }

            // If we found the median bar, draw the line through its center
            if (medianBarIndex >= 0) {
                // Calculate the center position of the median bar
                val medianBarCenterX = (medianBarIndex * barWidth) + (barWidth * 0.5f)

                // Draw the median line through the center of the bar
                drawLine(
                    color = Color(0xFFFF6F3F), // Orange color for visibility
                    start = Offset(medianBarCenterX, 0f), // Top of canvas
                    end = Offset(medianBarCenterX, canvasHeight), // Bottom of canvas
                    strokeWidth = 3.dp.toPx(), // Line thickness
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)) // Dashed pattern
                )
            }
        }
    }
}