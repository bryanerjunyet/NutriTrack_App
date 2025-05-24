package com.fit2081.junyet33521026

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.fit2081.junyet33521026.utils.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import kotlinx.coroutines.launch


/**
 * Login activity for the application.
 */
class LoginPage : ComponentActivity() {
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
                    LoginScreen(Modifier.padding(innerPadding), patientViewModel)
                }
            }
        }
    }
}


/**
 * Composable function for the UI of Login screen.
 *
 * @param modifier Modifier to be applied.
 * @param viewModel ViewModel to manage patient data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier, viewModel: PatientViewModel) {
    // current context to start activity
    val context = LocalContext.current
    // handle all user accounts as state
    val userAccounts by viewModel.allUserIds.collectAsState(initial = emptyList())

    // user ID input
    var userInputID by remember { mutableStateOf("") }
    // user ID dropdown options
    var userOptionsID by remember { mutableStateOf(false) }
    // input password
    var passwordInput by remember { mutableStateOf("") }
    // check password error
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()), // maximum size but space on all sides
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // NutriTrack Logo
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.nutritrack_logo2),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // Login title
        Text(
            text = "Login",
            textAlign = TextAlign.Center,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(16.dp))

        // User ID dropdown
        ExposedDropdownMenuBox(
            expanded = userOptionsID, // show dropdown menu
            onExpandedChange = { userOptionsID = !userOptionsID } // toggle dropdown menu
        ) {
            OutlinedTextField(
                value = userInputID, // current user ID login
                onValueChange = { }, // do not allow user to type in
                label = { Text("Select User ID") },
                readOnly = true, // only can select from dropdown menu
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(userOptionsID) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // show dropdown menu onto text field
            )
            DropdownMenu(
                expanded = userOptionsID,  // show dropdown menu
                onDismissRequest = { userOptionsID = false } // hide dropdown menu
            ) {
                userAccounts.forEach { userID ->
                    DropdownMenuItem(
                        text = { Text(userID) },
                        onClick = {
                            userInputID = userID
                            userOptionsID = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Password
        OutlinedTextField(
            value = passwordInput, // current password login
            onValueChange = {
                passwordInput = it
            },
            label = { Text("Enter Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(), // hide password
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password validation
        if (passwordError) {
            // Error message
            Text(
                text = "Incorrect password.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }

        // Login description
        Text(
            text = "This app is only for pre-registered users. Please enter your ID and password or register to claim your account on your first visit.",
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
        Button(
            onClick = {
                viewModel.viewModelScope.launch {
                    // successful login
                    if ((passwordInput.isNotEmpty() || userInputID.isNotEmpty()) && (viewModel.validateCredentials(userInputID, passwordInput)) ) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                        AuthManager.login(userInputID)
                        // navigate first visitor page or home page
                        navigateDecision(context, userInputID)
                    } else { // unsuccessful login
                        if (userInputID.isEmpty()) { // no user ID
                            Toast.makeText(context, "Invalid User ID", Toast.LENGTH_LONG).show()
                        } else if (passwordInput.isEmpty()) { // no password
                            Toast.makeText(context, "Invalid Password", Toast.LENGTH_LONG).show()
                        } else { // incorrect password
                            Toast.makeText(context, "Incorrect Credentials.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Register button
        Button(
            // navigate to registration page
            onClick = { context.startActivity(Intent(context, RegistrationPage::class.java)) },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
        ) {
            Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Navigate to the appropriate page based on the user's form completion status (first visitor).
 *
 * @param context Context to access shared preference.
 * @param userId User ID to be saved.
 */
fun navigateDecision(context: Context, userId: String) {
    val sharedPref = context.getSharedPreferences("${userId}Response", Context.MODE_PRIVATE)
    val completedResponse = sharedPref.getBoolean("completedResponse", false)
    if (completedResponse) {
        // navigate to home page (completed)
        context.startActivity(Intent(context, HomePage::class.java))
    } else {
        // navigate to questionnaire page (not completed)
        context.startActivity(Intent(context, QuestionnairePage::class.java))
    }
}

