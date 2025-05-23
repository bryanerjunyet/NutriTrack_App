package com.fit2081.junyet33521026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit2081.junyet33521026.data.UIState
import com.fit2081.junyet33521026.data.AIViewModel
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme

/**
 * Main activity for the clinician login and dashboard page.
 */
class ClinicianPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // State to control which screen to display
                    val isLoggedIn = remember { mutableStateOf(false) }

                    if (isLoggedIn.value) {
                        // Show dashboard when logged in
                        ClinicianDashboardScreen(
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        // Show login screen when not logged in
                        ClinicianLoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginSuccess = { isLoggedIn.value = true },
                            onBackClick = { finish() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Clinician Login Screen for the application.
 *
 * @param modifier Modifier to be applied.
 * @param onLoginSuccess Callback for successful login.
 * @param onBackClick Callback for back button click.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicianLoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Settings") }
    val clinicianKey = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // NutriTrack Logo
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.nutritrack_logo2),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // Clinician title
        Text(
            text = "Clinician Login",
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Clinician key input field
        OutlinedTextField(
            value = clinicianKey.value,
            onValueChange = {
                clinicianKey.value = it
                errorMessage.value = "" // Clear error when typing
            },
            label = { Text("Enter Clinician Key") },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        // Error message if any
        if (errorMessage.value.isNotEmpty()) {
            Text(
                text = errorMessage.value,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = {
                if (clinicianKey.value == "dollar-entry-apples") {
                    // Correct key, switch to dashboard
                    onLoginSuccess()
                } else {
                    // Incorrect key
                    errorMessage.value = "Invalid clinician key. Please try again."
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Back button
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }

    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }
}

/**
 * Clinician Dashboard Screen for the application.
 *
 * @param modifier Modifier to be applied.
 * @param onBackClick Callback for back button click.
 */
@Composable
fun ClinicianDashboardScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Settings") }

    // Get ViewModel instances
    val patientViewModel: PatientViewModel = viewModel(
        factory = PatientViewModel.PatientViewModelFactory(context)
    )
    val AIViewModel: AIViewModel = viewModel(
        factory = AIViewModel.AIViewModelFactory(context)
    )

    // Collect UI state
    val clinicianUiState by AIViewModel.uiState.collectAsState()

    // Collect average HEIFA scores
    val maleAverageHeifa by AIViewModel.maleAverageHeifa.collectAsState(initial = 0.0f)
    val femaleAverageHeifa by AIViewModel.femaleAverageHeifa.collectAsState(initial = 0.0f)

    // Initialize ViewModel data
    LaunchedEffect(key1 = true) {
        AIViewModel.calculateAverageScores()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Clinician Dashboard",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // HEIFA Average Score boxes
        HeifaScoreBox(
            title = "Average HEIFA (Male)",
            score = String.format("%.1f", maleAverageHeifa)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HeifaScoreBox(
            title = "Average HEIFA (Female)",
            score = String.format("%.1f", femaleAverageHeifa)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Find Data Pattern Button
        Button(
            onClick = { AIViewModel.analyzeData() },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search_icon),
                    contentDescription = "Search Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Find Data Pattern",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Analysis Results
        when (clinicianUiState) {
            is UIState.Initial -> {
                // Nothing to show yet
            }

            is UIState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    color = Color.Red
                )
            }

            is UIState.ClinicianSuccess -> {
                val insights = (clinicianUiState as UIState.ClinicianSuccess).insights
                insights.forEach { insight ->
                    InsightCard(title = insight.title, description = insight.description)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            is UIState.Error -> {
                Text(
                    text = "Error: ${(clinicianUiState as UIState.Error).errorMessage}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is UIState.NutriCoachSuccess -> {
                // Nothing to show
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }


}

@Composable
fun HeifaScoreBox(
    title: String,
    score: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$title:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = score,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun InsightCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp
            )
        }
    }
}