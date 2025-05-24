package com.fit2081.junyet33521026

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fit2081.junyet33521026.data.PatientViewModel
import com.fit2081.junyet33521026.ui.theme.JunYet33521026Theme
import kotlinx.coroutines.launch
import androidx.core.content.edit


/**
 * Main activity for the application.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // PatientViewModel setup to setup patient database
        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        // Database setup at first launch only
        val sharedPreferences = getSharedPreferences("NutriTrackSetup", Context.MODE_PRIVATE)
        val databaseSetup = sharedPreferences.getBoolean("completeSetup", false)
        if (!databaseSetup) {
            lifecycleScope.launch {
                patientViewModel.importPatientsFromCsv(this@MainActivity)
                sharedPreferences.edit() { putBoolean("completeSetup", true) }
            }
        }

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WelcomeScreen(Modifier.padding(innerPadding));
                }
            }
        }
    }
}


@Composable
/**
 * Composable function for the UI of Welcome Screen.
 *
 * @param modifier Modifier to be applied.
 */
fun WelcomeScreen(modifier: Modifier = Modifier) {
    // current context to start activity
    val context = LocalContext.current
    // uriHandler to open links
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.padding(32.dp).verticalScroll(rememberScrollState()), // maximum size but space on all sides
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // NutriTrack Logo
        Image(
            painter = painterResource(id = R.drawable.nutritrack_logo2),
            contentDescription = "NutriTrack Logo",
            modifier = Modifier.size(300.dp)
        )

        // App description
        Text(
            text = "This app provides general health and nutrition information for educational purposes only. It is not intended as medical advice, diagnosis, or treatment. Always consult a qualified healthcare professional before making any changes to your diet, exercise, or health regimen. Use this app at your own risk. If you’d like to an Accredited Practicing Dietitian (APD), please visit the Monash Nutrition/Dietetics Clinic (discounted rates for students):",
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Clickable link
        ClickableText(
            text = AnnotatedString("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition"),
            onClick = { uriHandler.openUri("https://www.monash.edu/medicine/scs/nutrition/clinics/nutrition") },
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(35.dp))

        // Login button
        Button(
            onClick = { context.startActivity(Intent(context, LoginPage::class.java)) }, // Navigate to LoginPage
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text = "Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(5.dp))

        // Developer info
        Text(text = "Er Jun Yet (33521026)", fontSize = 15.sp, fontWeight = FontWeight.Light)
    }
}

