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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                            modifier = Modifier.padding(innerPadding),
                            onBackClick = { isLoggedIn.value = false }
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

        // Spacer to push bottom navigation bar to bottom
        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Bar
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
fun ClinicianDashboardScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Settings") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Clinician Dashboard",
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Dashboard content placeholder
        Text(
            text = "Welcome to the Clinician Dashboard",
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        // Back button
        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Back to Settings",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Spacer to push bottom navigation bar to bottom
        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Bar
        BottomNavigationBar(context, currentPage)
    }
}