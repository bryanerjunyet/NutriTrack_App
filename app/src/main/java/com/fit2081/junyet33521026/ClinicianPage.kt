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
import kotlinx.coroutines.launch
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

        // PatientViewModel setup to manage patient data
        val patientViewModel = PatientViewModel(this)


        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // login state control
                    val isLoggedIn = remember { mutableStateOf(false) }

                    if (isLoggedIn.value) { // admin logged in success
                        ClinicianDashboardScreen(
                            modifier = Modifier.padding(innerPadding),
                            patientViewModel = patientViewModel
                        )
                    } else { // admin under validation
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

private val clinicianValidKey = "dollar-entry-apples"

/**
 * Composable function for the Clinician Login Screen.
 *
 * @param modifier Modifier to be applied.
 * @param onLoginSuccess Callback for successful login.
 * @param onBackClick Callback for back button click.
 */
@Composable
fun ClinicianLoginScreen(modifier: Modifier = Modifier, onLoginSuccess: () -> Unit, onBackClick: () -> Unit) {
    // current context and other state management
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

        // Clinician key input
        OutlinedTextField(
            value = clinicianKey.value,
            onValueChange = {
                clinicianKey.value = it
                errorMessage.value = ""
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

        // Error message display
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
                if (clinicianKey.value == clinicianValidKey) {
                    onLoginSuccess() // successful login
                } else { // incorrect key
                    errorMessage.value = "Invalid clinician key. Only admin can access this page."
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            )
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
            )
        ) {
            Text(
                text = "Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }

    // Bottom navigation bar
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }
}

/**
 * Composable function for the Clinician Dashboard Screen.
 *
 * @param modifier Modifier to be applied.
 */
@Composable
fun ClinicianDashboardScreen(modifier: Modifier = Modifier, patientViewModel: PatientViewModel) {
    // current context, page and coroutine scope management
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Settings") }
    val coroutineScope = rememberCoroutineScope()

    // AIViewModel setup for access to AI functionalities
    val AIViewModel: AIViewModel = viewModel(
        factory = AIViewModel.AIViewModelFactory(context)
    )

    // current average scores state
    val maleAverageHeifa = remember { mutableStateOf(0.0f) }
    val femaleAverageHeifa = remember { mutableStateOf(0.0f) }
    val clinicianUiState by AIViewModel.uiState.collectAsState()

    // launched effect to calculate average scores using PatientViewModel
    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            try {
                maleAverageHeifa.value = patientViewModel.getAverageHeifaScoreMale()
                femaleAverageHeifa.value = patientViewModel.getAverageHeifaScoreFemale()
            } catch (e: Exception) {
                // Handle potential exceptions
                maleAverageHeifa.value = 0.0f
                femaleAverageHeifa.value = 0.0f
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // scrollable content
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Clinician dashboard title
        Text(
            text = "Clinician Dashboard",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Male HEIFA average scores
        HeifaScoreBox(
            title = "Average HEIFA (Male)",
            score = String.format("%.1f", maleAverageHeifa.value)
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Female HEIFA average scores
        HeifaScoreBox(
            title = "Average HEIFA (Female)",
            score = String.format("%.1f", femaleAverageHeifa.value)
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Find data pattern Button
        Button(
            onClick = { AIViewModel.analyseData() },
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

        // AI insights result
        if (clinicianUiState is UIState.ClinicianLoading) { // visual feedback of circular loading indicator
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.Red
            )
        } // display all AI insights
        else if (clinicianUiState is UIState.ClinicianSuccess) {
            val insights = (clinicianUiState as UIState.ClinicianSuccess).insights
            insights.forEach { insight ->
                InsightCard(title = insight.title, description = insight.description)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } // display error message
        else if (clinicianUiState is UIState.ClinicianError) {
            Text(
                text = "Error: ${(clinicianUiState as UIState.ClinicianError).errorMessage}",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }
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
 * Composable function for HEIFA score box.
 *
 * @param title Title of HEIFA score box.
 * @param score Score to be displayed.
 */
fun HeifaScoreBox(title: String, score: String) {
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
/**
 * Composable function for displaying AI insights in a card format.
 *
 * @param title Title of the insight.
 * @param description Description of the insight.
 */
fun InsightCard(title: String, description: String) {
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