package com.fit2081.junyet33521026

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


class RegistrationPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegistrationScreen(
                        Modifier.padding(innerPadding),
                        patientViewModel,
                        onNavigateToLogin = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: PatientViewModel,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userAccounts by viewModel.allUserIds.collectAsState(initial = emptyList())

    var userInputID by remember { mutableStateOf("") }
    var userOptionsID by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // NutriTrack Logo
        Image(
            painter = painterResource(id = R.drawable.nutritrack_logo2),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(200.dp)
        )

        // Registration title
        Text(
            text = "Register Account",
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
                modifier = Modifier.fillMaxWidth().menuAnchor() // show dropdown menu onto text field
            )
            androidx.compose.material3.DropdownMenu(
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

        // Phone number field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password fields
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, top = 10.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Register description
        Text(
            text = "This app is only for pre-registered users. Enter your ID, phone number and password to claim your account.",
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Register button
        Button(
            onClick = {
                errorMessage = ""

                coroutineScope.launch {
                    Log.d("Registration", "Validation triggered")

                    if (userInputID.isEmpty()) {
                        errorMessage = "Please select a User ID"
                        return@launch
                    }

                    if (phoneNumber.isEmpty()) {
                        errorMessage = "Please enter your phone number"
                        return@launch
                    }

                    if (name.isEmpty()) {
                        errorMessage = "Name cannot be empty"
                        return@launch
                    }

                    if (password.isEmpty() || confirmPassword.isEmpty()) {
                        errorMessage = "Password fields cannot be empty"
                        return@launch
                    }

                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@launch
                    }

                    val isPhoneMatch = viewModel.validateRegistration(userInputID, phoneNumber)
                    Log.d("Registration", "Phone number match result: $isPhoneMatch")

                    if (!isPhoneMatch) {
                        errorMessage = "Phone number does not match with User ID"
                        return@launch
                    }

                    val success = viewModel.registerPatient(userInputID, phoneNumber, name, password)
                    Log.d("Registration", "Registration success: $success")
                    if (success) {
                        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                    } else {
                        errorMessage = "Registration failed. Please check your input."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Login button
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}