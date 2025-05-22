package com.fit2081.junyet33521026

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.fit2081.junyet33521026.data.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme

/**
 * Main activity for the application.
 */
class SettingsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SettingsPageScreen(
                        Modifier.padding(innerPadding),
                        patientViewModel
                    )
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPageScreen(
    modifier: Modifier = Modifier,
    viewModel: PatientViewModel
) {
    // Current context to start activity
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("Settings") }

    // Load current login user ID
    val currentUserID = AuthManager.currentUserId ?: return

    // State for user details
    val patientName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") }

    // Load user details from view model
    LaunchedEffect(currentUserID) {
        val patient = viewModel.getPatient(currentUserID)
        patientName.value = patient.name ?: ""
        phoneNumber.value = patient.phoneNumber ?: ""
        userId.value = patient.userId ?: ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Settings title
        Text(
            text = "Settings",
            textAlign = TextAlign.Center,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // ACCOUNT section
        Text(
            text = "ACCOUNT",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // User name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.account_icon),
                contentDescription = "Account",
                modifier = Modifier.size(25.dp),
                tint = Color.Red
            )
            Text(
                text = patientName.value,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Phone number
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.phone_icon),
                contentDescription = "Phone",
                modifier = Modifier.size(25.dp),
                tint = Color.Red
            )
            Text(
                text = formatPhoneNumber(phoneNumber.value),
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // User ID
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.id_icon),
                contentDescription = "ID",
                modifier = Modifier.size(25.dp),
                tint = Color.Red
            )
            Text(
                text = userId.value,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // Divider
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        // OTHER SETTINGS section
        Text(
            text = "OTHER SETTINGS",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Logout button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Logout user and navigate to login page
                    AuthManager.logout()
                    val intent = Intent(context, LoginPage::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.login_icon),
                    contentDescription = "Logout",
                    modifier = Modifier.size(25.dp),
                    tint = Color.Red
                )
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to Logout",
                tint = Color.Gray
            )
        }

        // Clinician Login button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Navigate to clinician login page
                    context.startActivity(Intent(context, ClinicianPage::class.java))
                }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.admin_icon),
                    contentDescription = "Clinician Login",
                    modifier = Modifier.size(25.dp),
                    tint = Color.Red
                )
                Text(
                    text = "Clinician Login",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to Clinician Login",
                tint = Color.Gray
            )
        }

    }


    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

}


fun formatPhoneNumber(phoneNumber: String): String {
    return if (phoneNumber.length >= 11) {
        "+${phoneNumber.substring(0, 2)} ${phoneNumber.substring(2, 5)} ${phoneNumber.substring(5, 8)} ${phoneNumber.substring(8)}"
    } else {
        phoneNumber // Return as is if not enough digits
    }
}