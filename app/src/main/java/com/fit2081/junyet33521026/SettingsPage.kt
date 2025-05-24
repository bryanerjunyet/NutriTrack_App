package com.fit2081.junyet33521026

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.fit2081.junyet33521026.utils.AuthManager
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme

/**
 * Settings activity for the application.
 */
class SettingsPage : ComponentActivity() {
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
                    SettingsPageScreen(Modifier.padding(innerPadding), patientViewModel)
                }
            }
        }
    }
}


/**
 * Composable function for the UI of Settings Page.
 *
 * @param modifier Modifier to be applied.
 */
@Composable
fun SettingsPageScreen(modifier: Modifier = Modifier, viewModel: PatientViewModel) {
    // current context to start activity
    val context = LocalContext.current
    // current login user ID
    val currentUserID = AuthManager.currentUserId ?: return

    // current state of pages
    val currentPage = remember { mutableStateOf("Settings") }
    // current state of user details
    val patientName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val userId = remember { mutableStateOf("") }

    // use LaunchedEffect to fetch patient details
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
            .verticalScroll(rememberScrollState()) // scrollable content
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

        // Account section
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
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray
        )

        // Other settings section
        Text(
            text = "OTHER SETTINGS",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // User logout button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // logout user and navigate to login page
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
                    // navigate to clinician login page
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

        Spacer(modifier = Modifier.height(100.dp))

    }

    // Bottom navigation bar
    Column {
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)
    }

}

/**
 * Formats a phone number string into +61 xxx xxx xxx format.
 *
 * @param phoneNumber The phone number string to format.
 * @return A formatted phone number string.
 */
fun formatPhoneNumber(phoneNumber: String): String {
    return if (phoneNumber.length >= 11) {
        "+${phoneNumber.substring(0, 2)} ${phoneNumber.substring(2, 5)} ${phoneNumber.substring(5, 8)} ${phoneNumber.substring(8)}"
    } else {
        phoneNumber
    }
}