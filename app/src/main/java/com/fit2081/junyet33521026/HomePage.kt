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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.fit2081.junyet33521026.utils.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme


/**
 * Home activity for the application.
 */
class HomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PatientViewModel setup to manage patient data
        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomePageScreen(
                        Modifier.padding(innerPadding), patientViewModel)
                }
            }
        }
    }
}


/**
 * Composable function for the UI of Home Page.
 *
 * @param modifier Modifier to be applied.
 * @param viewModel ViewModel to manage patient data.
 */
@Composable
fun HomePageScreen(modifier: Modifier = Modifier, viewModel: PatientViewModel) {
    // current context to start activity
    val context = LocalContext.current
    // current login user ID
    val currentUserID = AuthManager.currentUserId ?: return

    // current state attributes
    val currentPage = remember { mutableStateOf("Home") }
    val foodScore = remember { mutableStateOf(0f) }
    val patientName = remember { mutableStateOf("") }

    // use LaunchedEffect to load patient data
    LaunchedEffect(currentUserID) {
        val patient = viewModel.getPatient(currentUserID)
        foodScore.value = patient.heifaTotalScore
        patientName.value = patient.name ?: ""
    }

    Column(
        modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState()), // scrollable page
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting message
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Hello, ${patientName.value}",
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
                    // navigate to QuestionnairePage
                    context.startActivity(Intent(context, QuestionnairePage::class.java))
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
            modifier = Modifier
                .fillMaxWidth() // lengthen the card
                .background(
                    Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(16.dp)
                ) // light gray background
                .padding(14.dp), // padding inside the card
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
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        context.startActivity(Intent(context, InsightsPage::class.java))
                    } // navigate to InsightsPage
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
        Spacer(modifier = Modifier.height(100.dp))
    }

    // Bottom navigation bar
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

}


@Composable
/**
 * Composable function for the Bottom Navigation Bar.
 *
 * @param context Context to start activities.
 * @param currentPage MutableState to track the current page.
 */
fun BottomNavigationBar(context: Context, currentPage: MutableState<String>) {
    Column (modifier = Modifier.fillMaxWidth().background(Color.White)) {
        // Gray line separator
        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Home Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    currentPage.value = "Home"
                    context.startActivity(Intent(context, HomePage::class.java)) // navigate to HomePage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.home_icon_red),
                    contentDescription = "Home",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentPage.value == "Home") Color.Red else Color.Gray
                )
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    color = if (currentPage.value == "Home") Color.Red else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Insights Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    currentPage.value = "Insights"
                    context.startActivity(Intent(context, InsightsPage::class.java)) // navigate to InsightsPage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.insights_icon_red),
                    contentDescription = "Insights",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentPage.value == "Insights") Color.Red else Color.Gray
                )
                Text(
                    text = "Insights",
                    fontSize = 12.sp,
                    color = if (currentPage.value == "Insights") Color.Red else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // NutriCoach Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    currentPage.value = "NutriCoach"
                    context.startActivity(Intent(context, NutriCoachPage::class.java)) // navigate to
                // InsightsPage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.nutricoach_icon_red),
                    contentDescription = "NutriCoach",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentPage.value == "NutriCoach") Color.Red else Color.Gray
                )
                Text(
                    text = "NutriCoach",
                    fontSize = 12.sp,
                    color = if (currentPage.value == "NutriCoach") Color.Red else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Settings Button
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    currentPage.value = "Settings"
                    context.startActivity(Intent(context, SettingsPage::class.java)) // navigate to
                // InsightsPage
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_icon_red),
                    contentDescription = "Settings",
                    modifier = Modifier.size(25.dp),
                    tint = if (currentPage.value == "Settings") Color.Red else Color.Gray
                )
                Text(
                    text = "Settings",
                    fontSize = 12.sp,
                    color = if (currentPage.value == "Settings") Color.Red else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

        }
        Spacer(modifier = Modifier.height(30.dp))

    }

}


