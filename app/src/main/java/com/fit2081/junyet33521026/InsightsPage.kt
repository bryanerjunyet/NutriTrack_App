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
import androidx.compose.runtime.mutableStateListOf
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
        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InsightsPageScreen(Modifier.padding(innerPadding))
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
fun InsightsPageScreen(modifier: Modifier = Modifier) {
    // current context to start activity
    val context = LocalContext.current
    // load current login user ID
    val sharedPref = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE)
    val currentUserID = remember { sharedPref.getString("userLoginID", "") ?: "" }
    // load food score from CSV file
    val foodScores = remember { loadUserFoodScores(context, currentUserID, "nutritrack_users.csv") }
    // load total food score from CSV file
    val totalScore = foodScores.firstOrNull()?.second ?: 0f

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ) {

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
        foodScores.drop(1).forEachIndexed { index, (food, score) ->
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
                text = "${totalScore.toInt()}/100",
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
                    progress = { (totalScore / 100) },
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

                        for ((category, score) in foodScores.drop(1)) {
                            val fullScore = if (foodScores.indexOf(Pair(category, score)) <= 6) 10 else 5
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
        BottomNavigationBar(context)
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

/**
 * Function to load user individual food scores from a CSV file.
 *
 * @param context Context of access assets.
 * @param userID User ID to search.
 * @param fileName Name of the CSV file.
 * @return A list of pairs of food name and score.
 */
fun loadUserFoodScores(context: Context, userID: String, fileName: String): List<Pair<String, Float>> {
    val foodScores = mutableStateListOf<Pair<String, Float>>()
    val assets = context.assets
    // open the CSV file and read line by line
    try {
        // open CSV file from assets
        val inputStream = assets.open(fileName)
        // create reader
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.useLines { lines ->
            lines.drop(1).forEach { line -> // skip header row
                val values = line.split(",") // split each line into values
                if (values.size > 1) {
                    if (userID == values[1].trim()) {
                        val sex = values[2].trim()
                        if (sex == "Male") { // obtain food score in terms of male
                            foodScores.add("Total Score" to values[3].trim().toFloat())
                            // first 6 items having full score of 10
                            foodScores.add("Discretionary" to values[5].trim().toFloat())
                            foodScores.add("Meat" to values[36].trim().toFloat())
                            foodScores.add("Dairy" to values[40].trim().toFloat())
                            foodScores.add("Sugar" to values[54].trim().toFloat())
                            foodScores.add("Sodium" to values[43].trim().toFloat())
                            foodScores.add("Grains & Cereal" to (values[29].trim().toFloat() + values[33].trim().toFloat()))
                            // the rest having full score of 5
                            foodScores.add("Vegetables" to values[8].trim().toFloat())
                            foodScores.add("Fruits" to values[19].trim().toFloat())
                            foodScores.add("Alcohol" to values[46].trim().toFloat())
                            foodScores.add("Water" to values[49].trim().toFloat())
                            foodScores.add("Saturated Fat" to (values[57].trim().toFloat() + values[60].trim().toFloat()))
                        } else { // obtain food score in terms of female
                            foodScores.add("Total Score" to values[4].trim().toFloat())
                            // first 6 items having full score of 10
                            foodScores.add("Discretionary" to values[6].trim().toFloat())
                            foodScores.add("Meat" to values[37].trim().toFloat())
                            foodScores.add("Dairy" to values[41].trim().toFloat())
                            foodScores.add("Sugar" to values[55].trim().toFloat())
                            foodScores.add("Sodium" to values[44].trim().toFloat())
                            foodScores.add("Grains & Cereal" to (values[30].trim().toFloat() + values[34].trim().toFloat()))
                            // the rest having full score of 5
                            foodScores.add("Vegetables" to values[9].trim().toFloat())
                            foodScores.add("Fruits" to values[20].trim().toFloat())
                            foodScores.add("Alcohol" to values[47].trim().toFloat())
                            foodScores.add("Water" to values[50].trim().toFloat())
                            foodScores.add("Saturated Fat" to (values[58].trim().toFloat() + values[61].trim().toFloat()))
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return foodScores
}