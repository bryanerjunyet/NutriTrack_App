package com.fit2081.junyet33521026

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.junyet33521026.data.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import java.io.BufferedReader
import java.io.InputStreamReader


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
    val sharedPref = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE)
    val currentUserID = AuthManager.currentUserId ?: return
    // load food score from CSV file
    val foodScores = remember { mutableStateOf<List<Pair<String, Float>>>(emptyList()) }

    LaunchedEffect(currentUserID) {
        val patient = viewModel.getPatient(currentUserID)
        patient?.let {
            val scores = mutableListOf<Pair<String, Float>>()

            scores.add("Total Score" to patient.heifaTotalScore)
            scores.add("Discretionary" to patient.discretionaryHeifaScore)
            scores.add("Meat" to patient.meatAndAlternativesHeifaScore)
            scores.add("Dairy" to patient.dairyAndAlternativesHeifaScore)
            scores.add("Sugar" to patient.sugarHeifaScore)
            scores.add("Sodium" to patient.sodiumHeifaScore)
            scores.add("Grains & Cereal" to (patient.grainsAndCerealsHeifaScore + patient.wholegrainsHeifaScore))
            scores.add("Vegetables" to patient.vegetablesHeifaScore)
            scores.add("Fruits" to patient.fruitHeifaScore)
            scores.add("Alcohol" to patient.alcoholHeifaScore)
            scores.add("Water" to patient.waterHeifaScore)
            scores.add("Saturated Fat" to (patient.saturatedFatHeifaScore + patient.unsaturatedFatHeifaScore))

            foodScores.value = scores
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {
        val totalScore = foodScores.value.firstOrNull()?.second?.toInt() ?: 0

        // Insights title
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Insights: Food Score",
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
        Spacer(modifier = Modifier.height(16.dp))

        // Total Food Quality Score
        Column(
            modifier = Modifier
                .fillMaxWidth() // lengthen the card
                .background(Color(0xFFF0F0F0), shape = RoundedCornerShape(16.dp)) // light gray background
                .padding(16.dp), // padding inside the card
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Food Quality Score",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${totalScore}/100",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = when { // set color based on score
                    totalScore < 50 -> Color(0xFFBB0E01)
                    totalScore < 70 -> Color(0xff2962ff)
                    else -> Color(0xFF2E7D32)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Progress bar
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                LinearProgressIndicator(
                    progress = { (totalScore.toFloat() / 100) },
                    modifier = Modifier
                        .weight(0.6f) // lengthen the bar
                        .height(10.dp) // height of the bar
                        .clip(RoundedCornerShape(5.dp)), // rounded corners
                    color = when { // set color based on score
                        totalScore < 50 -> Color(0xFFBB0E01)
                        totalScore < 70 -> Color(0xff2962ff)
                        else -> Color(0xFF2E7D32)
                    },
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Share button
            Row (modifier = Modifier.fillMaxWidth(0.6f)) {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"

                        // Text message with all the insights details
                        val insightsText = StringBuilder()
                        insightsText.append("NutriTrack Food Insights\n\n")
                        insightsText.append("Total Food Score: ${totalScore.toInt()}/100\n\n")
                        insightsText.append("Food Categories Score:\n")

                        foodScores.value.drop(1).forEachIndexed { index, (category, score) ->
                            val fullScore = if (index < 6) 10 else 5
                            insightsText.append("- $category: ${score.toInt()}/$fullScore\n")
                        }
                        insightsText.append("\n\nShared from NutriTrack App")

                        shareIntent.putExtra(Intent.EXTRA_TEXT, insightsText.toString())
                        context.startActivity(Intent.createChooser(shareIntent, "Share your food insights via"))
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
                        Text(text = "Share with friends", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Improve my diet button
            Row (modifier = Modifier.fillMaxWidth(0.6f)) {
                Button(
                    onClick = { /* TO BE IMPLEMENTED */ },
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
                        Text(text = "Improve my diet", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Bottom Navigation Bar
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
