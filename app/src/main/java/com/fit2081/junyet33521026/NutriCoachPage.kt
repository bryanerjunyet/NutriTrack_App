package com.fit2081.junyet33521026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
class NutriCoachPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val patientViewModel = ViewModelProvider(
            this, PatientViewModel.PatientViewModelFactory(this)
        )[PatientViewModel::class.java]

        setContent {
            JunYet33521026Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NutriCoachPageScreen(
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
@Composable
fun NutriCoachPageScreen(
    modifier: Modifier = Modifier,
    viewModel: PatientViewModel
) {
    // current context to start activity
    val context = LocalContext.current
    val currentPage = remember { mutableStateOf("NutriCoach") }
    // load current login user ID
    val currentUserID = AuthManager.currentUserId ?: return
    // load food score from CSV file
    val foodScore = remember { mutableStateOf(0f) }
    val patientName = remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(14.dp)
    ) {
        // Questionnaire title
        Text(
            text = "NutriCoach",
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Navigation Bar
        Spacer(modifier = Modifier.weight(1f))
        BottomNavigationBar(context, currentPage)

    }
}