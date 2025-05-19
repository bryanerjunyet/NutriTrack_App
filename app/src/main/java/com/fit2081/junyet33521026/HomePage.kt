package com.fit2081.junyet33521026

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Main activity for the application.
 */
class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomePageScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}


/**
 * Composable function for the UI of Home Page.
 *
 * @param modifier Modifier to be applied.
 */
@Composable
fun HomePageScreen(modifier: Modifier = Modifier) {
    // current context to start activity
    val context = LocalContext.current
    // load current login user ID
    val sharedPref = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE)
    val currentUserID = remember { sharedPref.getString("userLoginID", "") ?: "" }
    // load food score from CSV file
    val foodScore = remember { loadUserTotalScore(context, currentUserID, "nutritrack_users.csv") }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting message
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Hello, $currentUserID",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit info description
            Text(
                text = "You have filled in your Food Intake Questionnaire, but you can change the details here.",
                fontSize = 12.5.sp,
                textAlign = TextAlign.Justify,
                modifier = Modifier.weight(0.8f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Edit button
            Button(
                onClick = {
                    context.startActivity(Intent(context, QuestionnairePage::class.java)) // navigate to QuestionnairePage
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 9.dp) // padding inside the button
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Questionnaire"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Edit", fontSize = 16.sp)
            }

        }
        Spacer(modifier = Modifier.height(14.dp))

        // Food plate image
        Image(
            painter = painterResource(id = R.drawable.balanced_diet),
            contentDescription = "Food Plate",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Food Quality Score
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth() // lengthen the card
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(16.dp)) // light gray background
                .padding(14.dp) // padding inside the card
        ) {
            Text(
                text = "Your Food Quality Score",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${foodScore.value.toInt()}/100",
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = when { // set color based on score
                    foodScore.value.toInt() < 50 -> Color(0xFFBB0E01)
                    foodScore.value.toInt() < 70 -> Color(0xff2962ff)
                    else -> Color(0xFF2E7D32)
                }
            )
            Text(
                text = "See all scores >",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp).clickable { context.startActivity(Intent(context, InsightsPage::class.java)) } // navigate to InsightsPage
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Food Quality Score Explanation
        Text(
            text = "What is the Food Quality Score?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your Food Quality Score provides a snapshot of how well your eating patterns " +
                    "align with established food guidelines, helping you identify both strengths " +
                    "and opportunities for improvement in your diet. \nThis personalised " +
                    "measurement considers various food groups including vegetables, fruits, " +
                    "whole grains, and proteins to give you practical insights for making " +
                    "healthier food choics.",
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Bottom Navigation Bar
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context)
    }
}


@Composable
/**
 * Composable function for the Bottom Navigation Bar.
 *
 * @param context Context to start activities.
 */
fun BottomNavigationBar(context: Context) {
    Column {
        // Gray line separator
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Home Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, HomePage::class.java)) // navigate to HomePage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icon_red),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Insights Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, InsightsPage::class.java)) // navigate to InsightsPage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.insights_icon_red),
                    contentDescription = "Insights",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Red
                )
                Text(
                    text = "Insights",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // NutriCoach Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // TO BE IMPLEMENTED
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.nutricoach_icon_red),
                    contentDescription = "NutriCoach",
                    modifier = Modifier.size(26.dp),
                    tint = Color.Red
                )
                Text(
                    text = "NutriCoach",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Settings Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    // TO BE IMPLEMENTED
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon_red),
                    contentDescription = "Settings",
                    modifier = Modifier.size(23.dp),
                    tint = Color.Red
                )
                Text(
                    text = "Settings",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


/**
 * Function to load user total score from CSV file.
 *
 * @param context Context to access assets.
 * @param userID User ID to search.
 * @param fileName Name of the CSV file.
 * @return User's food score.
 */
fun loadUserTotalScore(context: Context, userID: String, fileName: String): MutableState<Float> {
    val foodScore = mutableStateOf(0f)
    val assets = context.assets
    // open the CSV file and read line by line
    try {
        // open CSV file from assets
        val inputStream = assets.open(fileName)
        // create reader
        val reader = BufferedReader(InputStreamReader(inputStream))

        // read the header row to map column names
        val headerRow = reader.readLine() ?: return foodScore
        val headers = headerRow.split(",").map { it.trim() }
        val headerMap = headers.mapIndexed { index, header -> header to index }.toMap()

        reader.useLines { lines ->
            lines.forEach { line ->
                val values = line.split(",") // split each line into values
                // check row matches given user ID
                if (values.getOrNull(headerMap["User_ID"] ?: -1) == userID) {
                    val sex = values.getOrNull(headerMap["Sex"] ?: -1)
                    if (sex == "Male") {
                        foodScore.value = values.getOrNull(headerMap["HEIFAtotalscoreMale"] ?: -1)?.toFloatOrNull() ?: 0f
                    } else if (sex == "Female") {
                        foodScore.value = values.getOrNull(headerMap["HEIFAtotalscoreFemale"] ?: -1)?.toFloatOrNull() ?: 0f
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return foodScore
}

