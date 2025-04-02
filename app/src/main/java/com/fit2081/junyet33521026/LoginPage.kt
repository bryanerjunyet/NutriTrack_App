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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * Main activity for the application.
 */
class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}


/**
 * Composable function for the UI of Login screen.
 *
 * @param modifier Modifier to be applied.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    // current context to start activity
    val context = LocalContext.current
    // load user accounts from CSV file
    val userAccounts = remember { loadUserAccounts(context, "nutritrack_users.csv") }

    // user ID input
    var userInputID by remember { mutableStateOf("") }
    // user ID dropdown options
    var userOptionsID by remember { mutableStateOf(false) }
    // input phone number
    var phoneInputNumber by remember { mutableStateOf("") }
    // check phone number error
    var phoneNumberError by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp), // maximum size but space on all sides
        horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.fillMaxWidth().menuAnchor() // show dropdown menu onto text field
            )
            DropdownMenu(
                expanded = userOptionsID,  // show dropdown menu
                onDismissRequest = { userOptionsID = false } // hide dropdown menu
            ) {
                userAccounts.keys.forEach { userID ->
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

        // Phone number
        OutlinedTextField(
            value = phoneInputNumber, // current phone number
            onValueChange = {
                // phone number input
                phoneInputNumber = it
                // check email valid every value changes
                phoneNumberError = !isValidUser(userInputID, it, userAccounts) },
            label = { Text("Enter Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phoneNumberError,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Phone number validation
        if (phoneNumberError) {
            // Error message
            Text(
                text = "Phone number does not match.",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
            )
        }

        // Login description
        Text(
            text = "This app is only for pre-registered users. Please enter the ID and phone number handy before continuing.",
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Continue button
        Button(
            onClick = {
                // check username and password valid
                if (isValidUser(userInputID, phoneInputNumber, userAccounts)) {
                    // save current login userID to SharedPreference
                    saveUserID(context, userInputID)
                    // show success message
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    // navigate to QuestionnairePage
                    context.startActivity(Intent(context, QuestionnairePage::class.java))
                } else { // show error message
                    Toast.makeText(context, "Incorrect Credentials.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}


/**
 * Loads user accounts from a CSV file in the assets folder.
 * Only two columns: phone number and user ID.
 * @param context The context to access the assets.
 * @param fileName The name of the CSV file in the assets folder.
 * @return A map of user ID -> phone number.
 */
fun loadUserAccounts(context: Context, fileName: String): Map<String, String> {
    val userAccounts = mutableMapOf<String, String>()
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
                    val phoneNumber = values[0].trim('"')
                    val userID = values[1].trim()
                    // build a map of user ID -> phone number
                    userAccounts[userID] = phoneNumber
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return userAccounts
}


/**
 * Check provided user ID and phone number match.
 *
 * @param userID User ID to validate.
 * @param phoneNumber Phone number to validate.
 * @param userAccounts A map of user ID -> phone number.
 * @return True if user ID and phone number match, false otherwise.
 */
fun isValidUser(userID: String, phoneNumber: String, userAccounts: Map<String, String>): Boolean {
    return userAccounts[userID] == phoneNumber
}


/**
 * Save user ID to SharedPreference.
 *
 * @param context Context to access SharedPreference.
 * @param userID User ID to save.
 */
fun saveUserID(context: Context, userID: String) {
    val sharedPref = context.getSharedPreferences("UserLogin", Context.MODE_PRIVATE).edit()
    // save user ID to SharedPreference
    sharedPref.putString("userLoginID", userID)
    sharedPref.apply()
}