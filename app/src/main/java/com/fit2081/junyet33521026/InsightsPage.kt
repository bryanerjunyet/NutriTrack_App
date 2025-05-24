package com.fit2081.junyet33521026

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
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
import kotlin.math.abs


/**
 * Insights activity for the application.
 */
class InsightsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PatientViewModel setup for accessing patient data
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
    // current page state
    val currentPage = remember { mutableStateOf("Insights") }
    // current login user ID
    val currentUserID = AuthManager.currentUserId ?: return
    // current food scores state
    val foodScores = remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }
    val scoreStats = remember { mutableStateOf<ScoreStats?>(null) }

    // launch effect to load patient data and scores
    LaunchedEffect(currentUserID) {
        val patient = viewModel.getPatient(currentUserID)
        patient?.let {
            val scores = mutableListOf<Pair<String, Float>>()

            ////// Food Scores /////
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

            ///// Score Statistics /////
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

        // Insights bars
        foodScores.value.drop(1).forEachIndexed { index, (food, score) ->
            // skip first item (total score)
            // full score for first 6 items is 10, the rest is 5
            val fullScore = if (index <= 6) 10 else 5
            InsightsBar(food, score, fullScore)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(28.dp))


        // Total food quality score
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
                // Food quality score title
                Text(
                    text = "Total Food Quality Score",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Total score
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

        //   *** NEW FEATURE ***   //
        // Food Score Distribution Graph
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
                    // Food score distribution title
                    Text(
                        text = "Food Quality Score Distribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Food score distribution graph
                    ScoreDistributionGraph(
                        distribution = stats.distribution,
                        userScore = totalScore.toFloat(),
                        medianScore = stats.medianScore
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Minimum, median & maximum scores (from all users)
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

                    // Achieved percentile
                    Text(
                        text = "Better than ${stats.userPercentile.toInt()}% of users",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when { // set color based on percentile
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
                    onClick = {
                        context.startActivity(Intent(context, NutriCoachPage::class.java))
                    },
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
            Spacer(modifier = Modifier.height(100.dp))
        }

    }

    // Bottom navigation bar
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

//          *** NEW FEATURE ***           //
///////// Score Distribution Graph /////////
@Composable
/**
 * Composable function for the Score Distribution Graph.
 *
 * @param distribution List of score frequencies.
 * @param userScore User's total score.
 * @param medianScore Median score from all users.
 */
fun ScoreDistributionGraph(
    distribution: List<ScoreFrequency>,
    userScore: Float,
    medianScore: Float
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        // canvas dimensions
        val canvasWidth = size.width
        val canvasHeight = size.height

        // bar height and width
        val totalBars = distribution.size.coerceAtLeast(1)
        val barWidth = canvasWidth / totalBars
        val maxFrequency = distribution.maxOfOrNull { it.count } ?: 1
        val maxBarHeight = canvasHeight * 0.8f

        // Draw all bars
        distribution.forEachIndexed { index, scoreData ->
            // bar x and y axis position
            val xAxis = index * barWidth
            val barHeight = (scoreData.count.toFloat() / maxFrequency) * maxBarHeight
            val yAxis = canvasHeight - barHeight

            // bar colours
            val barColor =
                if (scoreData.heifaTotalScore.toInt() == userScore.toInt()) {
                    Color(0xFFBB0E01) // user score -> dark red
                } else {
                    Color(0xFFF8B6B6) // other scores -> light red
                }

            // Draw rectangle
            drawRect(
                color = barColor,
                topLeft = Offset(
                    x = xAxis + barWidth * 0.1f, // margin on left
                    y = yAxis
                ),
                size = Size(
                    width = barWidth * 0.8f, // margin on right
                    height = barHeight
                )
            )
        }

        // Draw median line
        if (distribution.isNotEmpty()) {
            // locate bar with median score
            var medianBarIndex = -1
            var closestDistance = Float.MAX_VALUE
            distribution.forEachIndexed { index, scoreData ->
                val distance = abs(scoreData.heifaTotalScore - medianScore)
                if (distance < closestDistance) {
                    closestDistance = distance
                    medianBarIndex = index
                }
            }

            // Draw median line center of bar
            if (medianBarIndex >= 0) {
                // median x axis position
                val medianBarCenterX = (medianBarIndex * barWidth) + (barWidth * 0.5f)

                // Draw dashed line
                drawLine(
                    color = Color(0xFFFF6F3F), // orange median line
                    start = Offset(medianBarCenterX, 0f), // top of canvas
                    end = Offset(medianBarCenterX, canvasHeight), // bottom of canvas
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f)) // dashed line
                )
            }
        }
    }
}